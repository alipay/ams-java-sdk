/*
 * The MIT License
 * Copyright © 2019 Alipay.com Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.alipay.ams.callbacks;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.domain.responses.PaymentCancelResponse;
import com.alipay.ams.domain.telemetry.Call;
import com.alipay.ams.job.JobExecutor;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public class PaymentCancelCallback extends Callback<PaymentCancelRequest, PaymentCancelResponse> {

    private PaymentStatusUpdateCallback paymentStatusUpdateCallback;

    /**
     * @param paymentContextSupport
     * @param paymentStatusUpdateCallback
     */
    public PaymentCancelCallback(PaymentContextSupport paymentContextSupport,
                                 PaymentStatusUpdateCallback paymentStatusUpdateCallback) {
        super(paymentContextSupport);
        this.paymentStatusUpdateCallback = paymentStatusUpdateCallback;
    }

    /**
     * 
     * A solution with data persistent feature like Redis would be strongly recommended.
     * 
     * @param context
     * @param settings
     */
    protected void scheduleALaterCancel(final AMSClient client, final PaymentContext context) {

        final int cancelCount = context.getCancelCount();

        JobExecutor.instance.scheduleCancelJob(context.getPaymentRequestId(),
            client.getSettings().cancelInterval[cancelCount], TimeUnit.SECONDS);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client, PaymentCancelRequest request) {
        retryOrAlarm(client, request);
    }

    /**
     * 
     * @param client
     * @param request
     */
    private void retryOrAlarm(AMSClient client, PaymentCancelRequest request) {

        PaymentContext context = getPaymentContextSupport().loadContextByPaymentRequestIdOrDefault(
            request.getPaymentRequestId(),
            new PaymentContext(request.getPaymentRequestId(), request.getAgentToken()));

        if (needFurtherCancel(context, client.getSettings())) {

            //schedule a later Inquiry
            scheduleALaterCancel(client, context);

        } else {

            reportPaymentCancelResultUnknown(request.getPaymentRequestId());
            paymentStatusUpdateCallback.reportCancelResultUnknown(client, request);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    public boolean needFurtherCancel(PaymentContext context, AMSSettings settings) {
        int cancelCount = context.getCancelCount();

        if (cancelCount + 1 > settings.maxCancelCount) {
            return false;
        }

        return true;
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, PaymentCancelRequest request, int code) {
        retryOrAlarm(client, request);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentCancelRequest request,
                          ResponseResult responseResult) {
        retryOrAlarm(client, request);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onUstatus(AMSClient client, PaymentCancelRequest request,
                          ResponseResult responseResult) {
        retryOrAlarm(client, request);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentCancelRequest request) {

        PaymentCancelResponse cancelResponse = new PaymentCancelResponse(requestURI,
            client.getSettings(), responseHeader, body);

        reportPaymentCanceled(request.getPaymentRequestId());

        paymentStatusUpdateCallback.onPaymentCancelled(
            StringUtil.defaultIfEmpty(cancelResponse.getPaymentRequestId(),
                request.getPaymentRequestId()),
            cancelResponse.getPaymentId(), cancelResponse.getCancelTime());
    }

    /**
     * Getter method for property <tt>paymentStatusUpdateCallback</tt>.
     * 
     * @return property value of paymentStatusUpdateCallback
     */
    public PaymentStatusUpdateCallback getPaymentStatusUpdateCallback() {
        return paymentStatusUpdateCallback;
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getCurrentCall(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return paymentContext.getTelemetry().getLatestUnfinishedCancelOrInitOne();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getPaymentRequestId(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getPaymentRequestId(PaymentCancelRequest request) {
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(PaymentCancelRequest request) {
        return request.getAgentToken();
    }
}
