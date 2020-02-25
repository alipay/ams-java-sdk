/*
 * The MIT License
 * Copyright © 2019 Alipay.com Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.alipay.ams.domain.requests;

import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.Asserts;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Body;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.Request;
import com.alipay.ams.util.DateUtil;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: EntryCodePaymentRequest.java, v 0.1 2019年11月5日 下午9:52:42 guangling.zgl Exp $
 */
public class EntryCodePaymentRequest extends Request {

    private String   paymentRequestId;
    private Order    order;
    private Currency currency;
    private Long     amountInCents;
    private String   paymentNotifyUrl;
    private String   paymentRedirectUrl;
    private Date     paymentExpiryTime;

    /**
     * @param settings
     * @param paymentRequestId
     * @param order
     * @param currency
     * @param amountInCents
     */
    public EntryCodePaymentRequest(AMSSettings settings, String paymentRequestId, Order order,
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
    public EntryCodePaymentRequest(AMSSettings settings, String paymentRequestId, Order order,
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

        if (StringUtil.isNotBlank(paymentNotifyUrl)) {
            body.put("paymentNotifyUrl", paymentNotifyUrl);
        }

        if (StringUtil.isNotBlank(paymentRedirectUrl)) {
            body.put("paymentRedirectUrl", paymentRedirectUrl);
        }

        if (paymentExpiryTime != null) {
            body.put("paymentExpiryTime", DateUtil.getISODateTimeStr(paymentExpiryTime));
        }

        if (order != null) {
            body.put("order", order);
        }

        Amount paymentAmount = new Amount(currency, amountInCents);
        body.put("paymentAmount", paymentAmount);

        Map<String, String> paymentMethod = new HashMap<String, String>();
        paymentMethod.put("paymentMethodType", "CONNECT_WALLET");
        body.put("paymentMethod", paymentMethod);

        Map<String, Object> paymentFactor = new HashMap<String, Object>();
        paymentFactor.put("inStorePaymentScenario", "EntryCode");
        body.put("paymentFactor", paymentFactor);

        return body;
    }

    /** 
     * @see com.alipay.ams.domain.Request#extValidate()
     */
    @Override
    protected boolean extValidate() {
        Asserts.notNull(order, "order");
        Asserts.notNull(order.getEnv(), "order.env");
        Asserts.notNull(order.getEnv().getUserAgent(), "order.env.userAgent");
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

    /**
     * Getter method for property <tt>paymentNotifyUrl</tt>.
     * 
     * @return property value of paymentNotifyUrl
     */
    public String getPaymentNotifyUrl() {
        return paymentNotifyUrl;
    }

    /**
     * Setter method for property <tt>paymentNotifyUrl</tt>.
     * 
     * @param paymentNotifyUrl value to be assigned to property paymentNotifyUrl
     */
    public void setPaymentNotifyUrl(String paymentNotifyUrl) {
        this.paymentNotifyUrl = paymentNotifyUrl;
    }

    /**
     * Getter method for property <tt>paymentExpiryTime</tt>.
     * 
     * @return property value of paymentExpiryTime
     */
    public Date getPaymentExpiryTime() {
        return paymentExpiryTime;
    }

    /**
     * Setter method for property <tt>paymentExpiryTime</tt>.
     * 
     * @param paymentExpiryTime value to be assigned to property paymentExpiryTime
     */
    public void setPaymentExpiryTime(Date paymentExpiryTime) {
        this.paymentExpiryTime = paymentExpiryTime;
    }

    /**
     * Getter method for property <tt>paymentRedirectUrl</tt>.
     * 
     * @return property value of paymentRedirectUrl
     */
    public String getPaymentRedirectUrl() {
        return paymentRedirectUrl;
    }

    /**
     * Setter method for property <tt>paymentRedirectUrl</tt>.
     * 
     * @param paymentRedirectUrl value to be assigned to property paymentRedirectUrl
     */
    public void setPaymentRedirectUrl(String paymentRedirectUrl) {
        this.paymentRedirectUrl = paymentRedirectUrl;
    }

}
