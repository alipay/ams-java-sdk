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
 * @version $Id: UserPresentedCodePaymentRequest.java, v 0.1 2019年10月16日 下午4:44:10 guangling.zgl Exp $
 */
public class UserPresentedCodePaymentRequest extends Request {

    private String   paymentRequestId;
    private Order    order;
    private Currency currency;
    private Long     amountInCents;
    private String   paymentCode;
    private String   paymentNotifyUrl;
    private Date     paymentExpiryTime;

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
        super.setAgentToken(agentToken);
    }

    /** 
     * @see com.alipay.ams.domain.Request#extValidate()
     */
    @Override
    protected boolean extValidate() {
        Asserts.notNull(order, "order required.");
        Asserts.notNull(order.getMerchant(), "order.merchant required.");
        Asserts.notNull(order.getOrderAmount(), "order.orderAmount required.");
        Asserts.notNull(order.getOrderDescription(), "order.orderDescription required.");
        Asserts.notNull(order.getMerchant().getReferenceMerchantId(),
            "order.merchant.referenceMerchantId required.");
        Asserts.notNull(order.getMerchant().getMerchantMCC(),
            "order.merchant.merchantMcc required.");
        Asserts.notNull(order.getMerchant().getMerchantName(),
            "order.merchant.merchantName required.");
        Asserts.notNull(order.getMerchant().getStore(), "order.merchant.store required.");
        Asserts.notNull(order.getMerchant().getStore().getReferenceStoreId(),
            "order.merchant.store.referenceStoreId required.");
        Asserts.notNull(order.getMerchant().getStore().getStoreName(),
            "order.merchant.store.storeName required.");
        Asserts.notNull(order.getMerchant().getStore().getStoreMCC(),
            "order.merchant.store.storeMcc required.");
        Asserts.notNull(order.getEnv(), "order.env required.");
        Asserts.notNull(order.getEnv().getStoreTerminalId(), "order.env.storeTerminalId required.");
        Asserts.notNull(order.getEnv().getStoreTerminalRequestTime(),
            "order.env.storeTerminalRequestTime required.");

        return true;
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

        if (StringUtil.isNotBlank(paymentNotifyUrl)) {
            body.put("paymentNotifyUrl", paymentNotifyUrl);
        }

        if (paymentExpiryTime != null) {
            body.put("paymentExpiryTime", DateUtil.getISODateTimeStr(paymentExpiryTime));
        }

        Amount paymentAmount = new Amount(currency, amountInCents);
        body.put("paymentAmount", paymentAmount);

        Map<String, String> paymentMethod = new HashMap<String, String>();
        paymentMethod.put("paymentMethodType", "CONNECT_WALLET");
        paymentMethod.put("paymentMethodId", paymentCode);
        body.put("paymentMethod", paymentMethod);

        Map<String, Object> paymentFactor = new HashMap<String, Object>();
        paymentFactor.put("inStorePaymentScenario", "PaymentCode");
        body.put("paymentFactor", paymentFactor);

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

}
