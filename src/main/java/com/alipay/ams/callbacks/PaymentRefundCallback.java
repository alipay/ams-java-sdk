/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.callbacks;

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentRefundRequest;
import com.alipay.ams.domain.responses.PaymentRefundResponse;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentRefundCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public abstract class PaymentRefundCallback extends
                                           Callback<PaymentRefundRequest, PaymentRefundResponse> {

    /**
     * 
     * @param cancelResponse
     */
    protected abstract void onRefundSuccess(PaymentRefundResponse cancelResponse);

    protected abstract void onRefundFailure(ResponseResult responseResult);

    protected abstract void onRefundCallFailure();

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
        onRefundCallFailure();
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
        onRefundCallFailure();
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentRefundRequest request,
                          ResponseResult responseResult) {
        onRefundFailure(responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentRefundRequest request) {
        PaymentRefundResponse refundResponse = new PaymentRefundResponse(requestURI,
            client.getSettings(), responseHeader, body);
        onRefundSuccess(refundResponse);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                          ResponseResult responseResult) {
        onRefundFailure(responseResult);
    }
}
