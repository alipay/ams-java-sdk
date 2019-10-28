/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public abstract class SinglePaymentInquiryCallback
                                                  extends
                                                  Callback<PaymentInquiryRequest, PaymentInquiryResponse> {

    public abstract void onPaymentInquiryResponse(PaymentInquiryResponse inquiryResponse);

    public abstract void onSingleInquiryFailure(ResponseResult responseResult);

    public abstract void onOrderNotExist(ResponseResult responseResult);

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client,
                              PaymentInquiryRequest paymentInquiryRequest) {
        onSingleInquiryFailure(null);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                                   int code) {
        onSingleInquiryFailure(null);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {

        if ("ORDER_NOT_EXIST".equals(responseResult.getResultCode())) {
            onOrderNotExist(responseResult);
        } else {
            onSingleInquiryFailure(responseResult);
        }

    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onUstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {
        onSingleInquiryFailure(responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.cfg.AMSSettings, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentInquiryRequest paymentInquiryRequest) {
        PaymentInquiryResponse inquiryResponse = new PaymentInquiryResponse(requestURI,
            client.getSettings(), responseHeader, body);

        onPaymentInquiryResponse(inquiryResponse);
    }
}
