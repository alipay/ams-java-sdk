/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.requests;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Body;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.Request;

/**
 * 
 * @author guangling.zgl
 * @version $Id: OrderCodePaymentRequest.java, v 0.1 2019年11月5日 下午9:52:42 guangling.zgl Exp $
 */
public class OrderCodePaymentRequest extends Request {

    private String   paymentRequestId;
    private Order    order;
    private Currency currency;
    private Long     amountInCents;

    /**
     * @param settings
     * @param paymentRequestId
     * @param order
     * @param currency
     * @param amountInCents
     */
    public OrderCodePaymentRequest(AMSSettings settings, String paymentRequestId, Order order,
                                   Currency currency, Long amountInCents) {
        this(settings, paymentRequestId, order, currency, amountInCents, null);

    }

    /**
     * @param settings
     * @param paymentRequestId
     * @param order
     * @param currency
     * @param amountInCents
     * @param agentToken
     */
    public OrderCodePaymentRequest(AMSSettings settings, String paymentRequestId, Order order,
                                   Currency currency, Long amountInCents, String agentToken) {

        super("/ams/api/v1/payments/pay", settings);
        this.paymentRequestId = paymentRequestId;
        this.order = order;
        this.currency = currency;
        this.amountInCents = amountInCents;
        super.setAgentToken(agentToken);
    }

    /** 
     * @see com.alipay.ams.domain.Request#buildBody()
     */
    @Override
    public Body buildBody() {

        Body body = new Body();

        body.put("productCode", "IN_STORE_PAYMENT");
        body.put("paymentRequestId", paymentRequestId);

        if (order != null) {
            body.put("order", order);
        }

        Amount paymentAmount = new Amount(currency, amountInCents);
        body.put("paymentAmount", paymentAmount);

        Map<String, String> paymentMethod = new HashMap<String, String>();
        paymentMethod.put("paymentMethodType", "CONNECT_WALLET");
        body.put("paymentMethod", paymentMethod);

        Map<String, Object> paymentFactor = new HashMap<String, Object>();
        paymentFactor.put("inStorePaymentScenario", "OrderCode");
        body.put("paymentFactor", paymentFactor);

        return body;
    }

    /** 
     * @see com.alipay.ams.domain.Request#extValidate()
     */
    @Override
    protected boolean extValidate() {
        return true;
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
     * Getter method for property <tt>order</tt>.
     * 
     * @return property value of order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Setter method for property <tt>order</tt>.
     * 
     * @param order value to be assigned to property order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Getter method for property <tt>currency</tt>.
     * 
     * @return property value of currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Setter method for property <tt>currency</tt>.
     * 
     * @param currency value to be assigned to property currency
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Getter method for property <tt>amountInCents</tt>.
     * 
     * @return property value of amountInCents
     */
    public Long getAmountInCents() {
        return amountInCents;
    }

    /**
     * Setter method for property <tt>amountInCents</tt>.
     * 
     * @param amountInCents value to be assigned to property amountInCents
     */
    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

}
