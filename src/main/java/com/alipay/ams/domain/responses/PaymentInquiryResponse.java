/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.responses;

import java.util.HashMap;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResponseHeader;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class PaymentInquiryResponse extends Response {

    private PaymentResultModel paymentResultModel;

    /**
     * @param requestURI
     * @param settings
     * @param responseHeader
     */
    public PaymentInquiryResponse(String requestURI, AMSSettings settings,
                                  ResponseHeader responseHeader, HashMap<String, Object> body) {
        super(requestURI, settings, responseHeader, body);
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap)
     */
    @Override
    protected void initBody(HashMap<String, Object> body) {
        this.paymentResultModel = new PaymentResultModel(body);
    }

    /** 
     * @see com.alipay.ams.domain.AMSMessage#getBizIdentifier()
     */
    @Override
    public String getBizIdentifier() {
        return paymentResultModel.getPaymentRequestId();
    }

    /**
     * Getter method for property <tt>paymentResultModel</tt>.
     * 
     * @return property value of paymentResultModel
     */
    public PaymentResultModel getPaymentResultModel() {
        return paymentResultModel;
    }

    /**
     * Setter method for property <tt>paymentResultModel</tt>.
     * 
     * @param paymentResultModel value to be assigned to property paymentResultModel
     */
    public void setPaymentResultModel(PaymentResultModel paymentResultModel) {
        this.paymentResultModel = paymentResultModel;
    }

}
