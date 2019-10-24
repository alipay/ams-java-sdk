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
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.Request;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: UserPresentedCodePaymentRequest.java, v 0.1 2019年10月16日 下午4:44:10 guangling.zgl Exp $
 */
public class UserPresentedCodePaymentRequest extends Request {

    private String   paymentRequestId;
    private Order    order;
    private Currency currency;
    private Long     amountInCents;
    private String   paymentCode;
    private String   agentToken;

    public UserPresentedCodePaymentRequest(AMSSettings settings, String paymentRequestId,
                                           Order order, Currency currency, Long amountInCents,
                                           String paymentCode) {
        this(settings, paymentRequestId, order, currency, amountInCents, paymentCode, null);

    }

    /**
     * @param requestURI
     * @param settings
     * @param paymentRequestId
     * @param order
     * @param currency
     * @param amountInCents
     * @param paymentCode
     */
    public UserPresentedCodePaymentRequest(AMSSettings settings, String paymentRequestId,
                                           Order order, Currency currency, Long amountInCents,
                                           String paymentCode, String agentToken) {
        super("/ams/api/v1/payments/pay", settings);
        this.paymentRequestId = paymentRequestId;
        this.order = order;
        this.currency = currency;
        this.amountInCents = amountInCents;
        this.paymentCode = paymentCode;
        this.agentToken = agentToken;

        updateBody();
        updateSignature();
    }

    /** 
     * @see com.alipay.ams.domain.Request#needExtraHeaders()
     */
    @Override
    protected Map<String, String> needExtraHeaders() {
        Map<String, String> extraHeaders = super.needExtraHeaders();
        extraHeaders.put("original_host", "open-sea.alipay.com");

        if (StringUtil.isNotBlank(this.agentToken)) {
            extraHeaders.put("Agent-Token", this.agentToken);
        }

        return extraHeaders;
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
        return "place-holder";
    }

    /** 
     * @see com.alipay.ams.domain.Request#getSdkVersion()
     */
    @Override
    protected String getSdkVersion() {
        return "1.0.20191016";
    }

    @Override
    protected void updateBody() {

        body.put("productCode", "IN_STORE_PAYMENT");
        body.put("paymentRequestId", paymentRequestId);
        if (order != null) {
            body.put("order", order);
        }

        Amount actualPaymentAmount = new Amount(currency, amountInCents);
        body.put("actualPaymentAmount", actualPaymentAmount);

        Amount paymentAmount = new Amount(currency, amountInCents);
        body.put("paymentAmount", paymentAmount);

        Map<String, String> paymentMethod = new HashMap<String, String>();
        paymentMethod.put("paymentMethodType", "CONNECT_WALLET");
        paymentMethod.put("paymentMethodId", paymentCode);
        body.put("paymentMethod", paymentMethod);

        Map<String, Boolean> paymentFactor = new HashMap<String, Boolean>();
        paymentFactor.put("isPaymentCode", true);
        body.put("paymentFactor", paymentFactor);
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

    /**
     * Getter method for property <tt>paymentCode</tt>.
     * 
     * @return property value of paymentCode
     */
    public String getPaymentCode() {
        return paymentCode;
    }

    /**
     * Setter method for property <tt>paymentCode</tt>.
     * 
     * @param paymentCode value to be assigned to property paymentCode
     */
    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    /**
     * Getter method for property <tt>agentToken</tt>.
     * 
     * @return property value of agentToken
     */
    public String getAgentToken() {
        return agentToken;
    }

    /**
     * Setter method for property <tt>agentToken</tt>.
     * 
     * @param agentToken value to be assigned to property agentToken
     */
    public void setAgentToken(String agentToken) {
        this.agentToken = agentToken;
    }
}
