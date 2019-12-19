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
import com.alipay.ams.domain.ResultStatusType;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.domain.telemetry.Call;
import com.alipay.ams.job.JobExecutor;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public class PaymentInquiryCallback extends Callback<PaymentInquiryRequest, PaymentInquiryResponse> {

    private PaymentCancelCallback paymentCancelCallback;

    /**
     * @param paymentCancelCallback
     */
    public PaymentInquiryCallback(PaymentCancelCallback paymentCancelCallback) {
        super(paymentCancelCallback.getPaymentContextSupport());
        this.paymentCancelCallback = paymentCancelCallback;
    }

    /**
     * 
     * A solution with data persistent feature like Redis would be strongly recommended.
     * 
     * @param amsSettings 
     * @param context 
     * 
     */
    public void scheduleALaterInquiry(final AMSClient client, final PaymentContext context) {

        int inquiryCount = context.getInquiryCount();

        JobExecutor.instance.scheduleInquiryJob(context.getPaymentRequestId(),
            client.getSettings().inquiryInterval[inquiryCount], TimeUnit.SECONDS);

    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client,
                              PaymentInquiryRequest paymentInquiryRequest) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                                   int code) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {

        if ("ORDER_NOT_EXIST".equals(responseResult.getResultCode())) {
            retryOrCancel(client, paymentInquiryRequest);
        } else {
            cancel(client, paymentInquiryRequest);
        }
    }

    /**
     * 
     */
    private void cancel(AMSClient client, PaymentInquiryRequest paymentInquiryRequest) {

        PaymentContext context = paymentCancelCallback.getPaymentContextSupport()
            .loadContextByPaymentRequestIdOrDefault(
                paymentInquiryRequest.getPaymentRequestId(),
                new PaymentContext(paymentInquiryRequest.getPaymentRequestId(),
                    paymentInquiryRequest.getAgentToken()));

        paymentCancelCallback.scheduleALaterCancel(client, context);
    }

    /**
     * @param client 
     * 
     */
    private void retryOrCancel(AMSClient client, PaymentInquiryRequest paymentInquiryRequest) {

        PaymentContext context = paymentCancelCallback.getPaymentContextSupport()
            .loadContextByPaymentRequestIdOrDefault(
                paymentInquiryRequest.getPaymentRequestId(),
                new PaymentContext(paymentInquiryRequest.getPaymentRequestId(),
                    paymentInquiryRequest.getAgentToken()));

        if (needFurtherInquiry(context, client.getSettings())) {

            //schedule a later Inquiry
            scheduleALaterInquiry(client, context);

        } else {
            cancel(client, paymentInquiryRequest);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    public boolean needFurtherInquiry(PaymentContext context, AMSSettings settings) {
        int inquiryCount = context.getInquiryCount();

        if (inquiryCount + 1 > settings.maxInquiryCount) {
            return false;
        }

        return true;
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onUstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.cfg.AMSSettings, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentInquiryRequest paymentInquiryRequest) {
        PaymentInquiryResponse inquiryResponse = new PaymentInquiryResponse(requestURI,
            client.getSettings(), responseHeader, body);

        switch (inquiryResponse.getPaymentResultModel().getPaymentStatus()) {
            case SUCCESS:

                reportPaymentS(paymentInquiryRequest.getPaymentRequestId());

                paymentCancelCallback.getPaymentStatusUpdateCallback().handlePaymentSuccess(
                    inquiryResponse.getPaymentResultModel());
                break;

            case FAIL:

                reportPaymentF(paymentInquiryRequest.getPaymentRequestId());

                paymentCancelCallback.getPaymentStatusUpdateCallback().onPaymentFailed(
                    inquiryResponse.getPaymentResultModel().getPaymentRequestId(),
                    new ResponseResult("UNKNOWN", ResultStatusType.F,
                        "paymentStatus = FAIL in Inquiry response."));
                break;

            case CANCELLED:

                reportPaymentCanceled(paymentInquiryRequest.getPaymentRequestId());

                paymentCancelCallback.getPaymentStatusUpdateCallback().onPaymentCancelled(
                    inquiryResponse.getPaymentResultModel().getPaymentRequestId(),
                    inquiryResponse.getPaymentResultModel().getPaymentId(), null);//cancelTime not available from an Inquiry.
                break;

            case PROCESSING:

                retryOrCancel(client, paymentInquiryRequest);
                break;

            default:
                break;
        }
    }

    /**
     * Getter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @return property value of paymentCancelCallback
     */
    public PaymentCancelCallback getPaymentCancelCallback() {
        return paymentCancelCallback;
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getCurrentCall(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return paymentContext.getTelemetry().getLatestUnfinishedInquiryOrInitOne();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getPaymentRequestId(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getPaymentRequestId(PaymentInquiryRequest request) {

        if (StringUtil.isBlank(request.getPaymentRequestId())) {
            throw new IllegalStateException(
                "paymentRequestId required when using PaymentInquiryCallback. Try using SinglePaymentInquiryCallback instead.");
        }
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(PaymentInquiryRequest request) {
        return request.getAgentToken();
    }
}
