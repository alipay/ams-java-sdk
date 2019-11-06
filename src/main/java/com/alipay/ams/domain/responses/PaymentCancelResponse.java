/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.responses;

import java.util.HashMap;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResponseHeader;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class PaymentCancelResponse extends Response {

    private String paymentRequestId;
    private String paymentId;
    private String cancelTime;

    /**
     * @param requestURI
     * @param settings
     * @param responseHeader
     */
    public PaymentCancelResponse(String requestURI, AMSSettings settings,
                                 ResponseHeader responseHeader, HashMap<String, Object> body) {
        super(requestURI, settings, responseHeader, body);
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap, com.alipay.ams.domain.ResponseHeader)
     */
    @Override
    protected void initBody(HashMap<String, Object> body, ResponseHeader responseHeader) {

        this.cancelTime = (String) body.get("cancelTime");
        this.paymentId = (String) body.get("paymentId");
        this.paymentRequestId = (String) body.get("paymentRequestId");
    }

    /** 
     * @see com.alipay.ams.domain.AMSMessage#getBizIdentifier()
     */
    @Override
    public String getBizIdentifier() {
        return paymentRequestId;
    }

    /**
     * Getter method for property <tt>paymentRequestId</tt>.
     * 
     * @return property value of paymentRequestId
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     * Setter method for property <tt>paymentRequestId</tt>.
     * 
     * @param paymentRequestId value to be assigned to property paymentRequestId
     */
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    /**
     * Getter method for property <tt>paymentId</tt>.
     * 
     * @return property value of paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Setter method for property <tt>paymentId</tt>.
     * 
     * @param paymentId value to be assigned to property paymentId
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Getter method for property <tt>cancelTime</tt>.
     * 
     * @return property value of cancelTime
     */
    public String getCancelTime() {
        return cancelTime;
    }

    /**
     * Setter method for property <tt>cancelTime</tt>.
     * 
     * @param cancelTime value to be assigned to property cancelTime
     */
    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

}
