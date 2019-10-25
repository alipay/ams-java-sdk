/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class PaymentResultModel {

    private String            paymentRequestId;
    private String            paymentId;
    private Amount            paymentAmount;
    private Amount            actualPaymentAmount;
    private String            paymentTime;
    private String            paymentCreateTime;

    private String            pspName;
    private String            pspCustomerId;
    private String            dispayCustomerId;

    private PaymentStatusType paymentStatus;
    private Amount            nonGuaranteeCouponAmount;
    private String            extendInfo;

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap)
     */
    @SuppressWarnings("unchecked")
    public PaymentResultModel(HashMap<String, Object> body) {

        this.paymentCreateTime = (String) body.get("paymentCreateTime");
        this.paymentId = (String) body.get("paymentId");
        this.paymentRequestId = (String) body.get("paymentRequestId");
        this.paymentTime = (String) body.get("paymentTime");

        this.paymentAmount = Amount.fromMap((Map<String, String>) body.get("paymentAmount"));
        this.actualPaymentAmount = Amount.fromMap((Map<String, String>) body
            .get("actualPaymentAmount"));
        this.nonGuaranteeCouponAmount = Amount.fromMap((Map<String, String>) body
            .get("nonGuaranteeCouponAmount"));

        Map<String, String> cust = (Map<String, String>) body.get("pspCustomerInfo");
        this.dispayCustomerId = cust.get("dispayCustomerId");
        this.pspCustomerId = cust.get("pspCustomerId");
        this.pspName = cust.get("pspName");

        this.paymentStatus = PaymentStatusType.valueOf((String) body.get("paymentStatus"));

        this.extendInfo = (String) body.get("extendInfo");

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
     * Getter method for property <tt>dispayCustomerId</tt>.
     * 
     * @return property value of dispayCustomerId
     */
    public String getDispayCustomerId() {
        return dispayCustomerId;
    }

    /**
     * Setter method for property <tt>dispayCustomerId</tt>.
     * 
     * @param dispayCustomerId value to be assigned to property dispayCustomerId
     */
    public void setDispayCustomerId(String dispayCustomerId) {
        this.dispayCustomerId = dispayCustomerId;
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

}
