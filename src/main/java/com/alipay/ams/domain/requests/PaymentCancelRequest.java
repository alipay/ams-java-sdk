/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.requests;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Request;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelRequest.java, v 0.1 2019年10月18日 下午5:58:58 guangling.zgl Exp $
 */
public class PaymentCancelRequest extends Request {

    private String  paymentId;
    private String  paymentRequestId;
    private boolean byPaymentId;

    public static PaymentCancelRequest byPaymentId(AMSSettings settings, String paymentId) {
        return new PaymentCancelRequest(settings, true, paymentId);
    }

    public static PaymentCancelRequest byPaymentRequestId(AMSSettings settings,
                                                          String paymentRequestId) {
        return new PaymentCancelRequest(settings, false, paymentRequestId);
    }

    /**
     * @param requestURI
     * @param settings
     */
    private PaymentCancelRequest(AMSSettings settings, boolean byPaymentId, String id) {

        super("/ams/api/v1/payments/cancel", settings);

        this.byPaymentId = byPaymentId;

        if (byPaymentId) {
            this.paymentId = id;
        } else {
            this.paymentRequestId = id;
        }

        updateBody();
        updateSignature();
    }

    /** 
     * @see com.alipay.ams.domain.Request#updateBody()
     */
    @Override
    protected void updateBody() {

        if (byPaymentId) {
            body.put("paymentId", paymentId);
        } else {
            body.put("paymentRequestId", paymentRequestId);
        }

    }

    /** 
     * @see com.alipay.ams.domain.AMSMessage#getBizIdentifier()
     */
    @Override
    public String getBizIdentifier() {
        return paymentRequestId;
    }

    /** 
     * @see com.alipay.ams.domain.Request#extValidate()
     */
    @Override
    protected boolean extValidate() {
        return true;
    }

    /** 
     * @see com.alipay.ams.domain.Request#getExt()
     */
    @Override
    protected String getExt() {
        return null;
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
     * Getter method for property <tt>byPaymentId</tt>.
     * 
     * @return property value of byPaymentId
     */
    public boolean isByPaymentId() {
        return byPaymentId;
    }

    /**
     * Setter method for property <tt>byPaymentId</tt>.
     * 
     * @param byPaymentId value to be assigned to property byPaymentId
     */
    public void setByPaymentId(boolean byPaymentId) {
        this.byPaymentId = byPaymentId;
    }

}
