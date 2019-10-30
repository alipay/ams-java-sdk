/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

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
import com.alipay.ams.job.JobExecutor;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public class PaymentCancelCallback extends Callback<PaymentCancelRequest, PaymentCancelResponse> {

    private PaymentContextCallback      paymentContextCallback;
    private PaymentStatusUpdateCallback paymentStatusUpdateCallback;

    /**
     * @param paymentContextCallback
     * @param paymentStatusUpdateCallback
     */
    public PaymentCancelCallback(PaymentContextCallback paymentContextCallback,
                                 PaymentStatusUpdateCallback paymentStatusUpdateCallback) {

        this.paymentContextCallback = paymentContextCallback;
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

        PaymentContext context = paymentContextCallback.loadContextByPaymentRequestIdOrDefault(
            request.getPaymentRequestId(), new PaymentContext(request.getPaymentRequestId(),
                request.getAgentToken()));

        if (needFurtherCancel(context, client.getSettings())) {

            //schedule a later Inquiry
            scheduleALaterCancel(client, context);

        } else {
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

        paymentStatusUpdateCallback.onPaymentCancelled(cancelResponse.getPaymentRequestId(),
            cancelResponse.getPaymentId(), cancelResponse.getCancelTime());
    }

    /**
     * Getter method for property <tt>paymentContextCallback</tt>.
     * 
     * @return property value of paymentContextCallback
     */
    public PaymentContextCallback getPaymentContextCallback() {
        return paymentContextCallback;
    }
}
