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
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class PaymentResultModel {

    private String            agentToken;

    private String            paymentRequestId;
    private String            paymentId;
    private Amount            paymentAmount;
    private Amount            actualPaymentAmount;
    private String            paymentTime;
    private String            paymentCreateTime;

    private String            pspName;
    private String            pspCustomerId;
    private String            displayCustomerId;

    private PaymentStatusType paymentStatus;
    private Amount            nonGuaranteeCouponAmount;

    private Amount            grossSettlementAmount;
    private Quote             settlementQuote;

    private String            extendInfo;

    private Quote             paymentQuote;

    /**
     * 
     */
    public PaymentResultModel(HashMap<String, Object> body, String agentToken) {
        this(body);
        this.agentToken = agentToken;
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap)
     */
    @SuppressWarnings("unchecked")
    private PaymentResultModel(HashMap<String, Object> body) {

        this.paymentCreateTime = (String) body.get("paymentCreateTime");
        this.paymentId = (String) body.get("paymentId");
        this.paymentRequestId = (String) body.get("paymentRequestId");
        this.paymentTime = (String) body.get("paymentTime");

        this.paymentAmount = Amount.fromMap((Map<String, String>) body.get("paymentAmount"));
        this.actualPaymentAmount = Amount.fromMap((Map<String, String>) body
            .get("actualPaymentAmount"));
        this.nonGuaranteeCouponAmount = Amount.fromMap((Map<String, String>) body
            .get("nonGuaranteeCouponAmount"));

        this.grossSettlementAmount = Amount.fromMap((Map<String, String>) body
            .get("grossSettlementAmount"));
        this.settlementQuote = Quote.fromMap((Map<String, Object>) body.get("settlementQuote"));

        Map<String, String> cust = (Map<String, String>) body.get("pspCustomerInfo");
        this.displayCustomerId = cust.get("displayCustomerId");
        this.pspCustomerId = cust.get("pspCustomerId");
        this.pspName = cust.get("pspName");

        if ("PAYMENT_RESULT".equals(body.get("notifyType")) && body.get("paymentStatus") == null) {
            this.paymentStatus = PaymentStatusType.SUCCESS;
        } else {
            this.paymentStatus = PaymentStatusType.valueOf((String) body.get("paymentStatus"));
        }

        this.extendInfo = (String) body.get("extendInfo");
        this.paymentQuote = Quote.fromMap((Map<String, Object>) body.get("paymentQuote"));

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
     * Getter method for property <tt>paymentAmount</tt>.
     * 
     * @return property value of paymentAmount
     */
    public Amount getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Setter method for property <tt>paymentAmount</tt>.
     * 
     * @param paymentAmount value to be assigned to property paymentAmount
     */
    public void setPaymentAmount(Amount paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Getter method for property <tt>paymentTime</tt>.
     * 
     * @return property value of paymentTime
     */
    public String getPaymentTime() {
        return paymentTime;
    }

    /**
     * Setter method for property <tt>paymentTime</tt>.
     * 
     * @param paymentTime value to be assigned to property paymentTime
     */
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    /**
     * Getter method for property <tt>paymentCreateTime</tt>.
     * 
     * @return property value of paymentCreateTime
     */
    public String getPaymentCreateTime() {
        return paymentCreateTime;
    }

    /**
     * Setter method for property <tt>paymentCreateTime</tt>.
     * 
     * @param paymentCreateTime value to be assigned to property paymentCreateTime
     */
    public void setPaymentCreateTime(String paymentCreateTime) {
        this.paymentCreateTime = paymentCreateTime;
    }

    /**
     * Getter method for property <tt>pspName</tt>.
     * 
     * @return property value of pspName
     */
    public String getPspName() {
        return pspName;
    }

    /**
     * Setter method for property <tt>pspName</tt>.
     * 
     * @param pspName value to be assigned to property pspName
     */
    public void setPspName(String pspName) {
        this.pspName = pspName;
    }

    /**
     * Getter method for property <tt>pspCustomerId</tt>.
     * 
     * @return property value of pspCustomerId
     */
    public String getPspCustomerId() {
        return pspCustomerId;
    }

    /**
     * Setter method for property <tt>pspCustomerId</tt>.
     * 
     * @param pspCustomerId value to be assigned to property pspCustomerId
     */
    public void setPspCustomerId(String pspCustomerId) {
        this.pspCustomerId = pspCustomerId;
    }

    /**
     * Getter method for property <tt>paymentStatus</tt>.
     * 
     * @return property value of paymentStatus
     */
    public PaymentStatusType getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Setter method for property <tt>paymentStatus</tt>.
     * 
     * @param paymentStatus value to be assigned to property paymentStatus
     */
    public void setPaymentStatus(PaymentStatusType paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Getter method for property <tt>nonGuaranteeCouponAmount</tt>.
     * 
     * @return property value of nonGuaranteeCouponAmount
     */
    public Amount getNonGuaranteeCouponAmount() {
        return nonGuaranteeCouponAmount;
    }

    /**
     * Setter method for property <tt>nonGuaranteeCouponAmount</tt>.
     * 
     * @param nonGuaranteeCouponAmount value to be assigned to property nonGuaranteeCouponAmount
     */
    public void setNonGuaranteeCouponAmount(Amount nonGuaranteeCouponAmount) {
        this.nonGuaranteeCouponAmount = nonGuaranteeCouponAmount;
    }

    /**
     * Getter method for property <tt>extendInfo</tt>.
     * 
     * @return property value of extendInfo
     */
    public String getExtendInfo() {
        return extendInfo;
    }

    /**
     * Setter method for property <tt>extendInfo</tt>.
     * 
     * @param extendInfo value to be assigned to property extendInfo
     */
    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    /**
     * Getter method for property <tt>actualPaymentAmount</tt>.
     * 
     * @return property value of actualPaymentAmount
     */
    public Amount getActualPaymentAmount() {
        return actualPaymentAmount;
    }

    /**
     * Setter method for property <tt>actualPaymentAmount</tt>.
     * 
     * @param actualPaymentAmount value to be assigned to property actualPaymentAmount
     */
    public void setActualPaymentAmount(Amount actualPaymentAmount) {
        this.actualPaymentAmount = actualPaymentAmount;
    }

    /**
     * Getter method for property <tt>displayCustomerId</tt>.
     * 
     * @return property value of displayCustomerId
     */
    public String getDisplayCustomerId() {
        return displayCustomerId;
    }

    /**
     * Setter method for property <tt>displayCustomerId</tt>.
     * 
     * @param displayCustomerId value to be assigned to property displayCustomerId
     */
    public void setDisplayCustomerId(String displayCustomerId) {
        this.displayCustomerId = displayCustomerId;
    }

    /**
     * Getter method for property <tt>paymentQuote</tt>.
     * 
     * @return property value of paymentQuote
     */
    public Quote getPaymentQuote() {
        return paymentQuote;
    }

    /**
     * Setter method for property <tt>paymentQuote</tt>.
     * 
     * @param paymentQuote value to be assigned to property paymentQuote
     */
    public void setPaymentQuote(Quote paymentQuote) {
        this.paymentQuote = paymentQuote;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

    /**
     * Getter method for property <tt>grossSettlementAmount</tt>.
     * 
     * @return property value of grossSettlementAmount
     */
    public Amount getGrossSettlementAmount() {
        return grossSettlementAmount;
    }

    /**
     * Setter method for property <tt>grossSettlementAmount</tt>.
     * 
     * @param grossSettlementAmount value to be assigned to property grossSettlementAmount
     */
    public void setGrossSettlementAmount(Amount grossSettlementAmount) {
        this.grossSettlementAmount = grossSettlementAmount;
    }

    /**
     * Getter method for property <tt>settlementQuote</tt>.
     * 
     * @return property value of settlementQuote
     */
    public Quote getSettlementQuote() {
        return settlementQuote;
    }

    /**
     * Setter method for property <tt>settlementQuote</tt>.
     * 
     * @param settlementQuote value to be assigned to property settlementQuote
     */
    public void setSettlementQuote(Quote settlementQuote) {
        this.settlementQuote = settlementQuote;
    }

}
