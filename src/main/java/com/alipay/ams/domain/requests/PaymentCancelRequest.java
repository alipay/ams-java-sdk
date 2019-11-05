/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.requests;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Body;
import com.alipay.ams.domain.Request;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelRequest.java, v 0.1 2019年10月18日 下午5:58:58 guangling.zgl Exp $
 */
public class PaymentCancelRequest extends Request {

    private String paymentRequestId;

    /**
     * @param settings
     * @param paymentRequestId
     */
    public PaymentCancelRequest(AMSSettings settings, String paymentRequestId) {

        this(settings, paymentRequestId, null);

    }

    /**
     * @param settings
     * @param paymentRequestId
     * @param agentToken
     */
    public PaymentCancelRequest(AMSSettings settings, String paymentRequestId, String agentToken) {

        super("/ams/api/v1/payments/cancel", settings);
        super.setAgentToken(agentToken);
        this.paymentRequestId = paymentRequestId;

    }

    /** 
     * @see com.alipay.ams.domain.Request#buildBody()
     */
    @Override
    public Body buildBody() {

        Body body = new Body();
        body.put("paymentRequestId", paymentRequestId);

        return body;
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

}
