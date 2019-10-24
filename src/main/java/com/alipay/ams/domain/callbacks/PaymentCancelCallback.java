/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.reponses.PaymentCancelResponse;
import com.alipay.ams.domain.requests.PaymentCancelRequest;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public abstract class PaymentCancelCallback extends
                                           Callback<PaymentCancelRequest, PaymentCancelResponse> {

    private PaymentContextCallback paymentContextCallback;

    /**
     * @param paymentContextCallback
     */
    public PaymentCancelCallback(PaymentContextCallback paymentContextCallback) {
        this.paymentContextCallback = paymentContextCallback;
    }

    /**
     * 
     * @param cancelResponse
     */
    protected abstract void onCancelSuccess(PaymentCancelResponse cancelResponse);

    /**
     * 
     * @param responseResult
     */
    protected abstract void onCancelFailure(ResponseResult responseResult);

    /**
     * 
     * @param client
     * @param request
     */
    protected abstract void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request);

    /**
     * 
     * @param context
     * @param settings
     */
    protected abstract void scheduleALaterCancel(PaymentContext context, AMSSettings settings);

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

        PaymentContext context = paymentContextCallback.loadContextByPaymentRequestId(request
            .getPaymentRequestId());

        if (needFurtherCancel(context, client.getSettings())) {

            //schedule a later Inquiry
            scheduleALaterCancel(context, client.getSettings());

        } else {
            reportCancelResultUnknown(client, request);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    private boolean needFurtherCancel(PaymentContext context, AMSSettings settings) {
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
        onCancelFailure(responseResult);
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
        onCancelSuccess(cancelResponse);
    }
}
