/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alipay.ams.domain.telemetry.Telemetry;
import com.google.gson.Gson;

/**
 * 
 * Context info for a payment.
 * 
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContext.java, v 0.1 2019年10月23日 下午3:52:13 guangling.zgl Exp $
 */
public class PaymentContext implements Serializable {
    /**  */
    private static final long serialVersionUID = 5669984777395423776L;

    private int               inquiryCount;
    private int               cancelCount;
    private int               orderCodeRequestCount;
    private Date              lastInquiryTime;
    private String            paymentRequestId;
    private String            agentToken;

    private Telemetry         telemetry;

    /**
     * @param paymentRequestId
     * @param agentToken
     */
    public PaymentContext(String paymentRequestId, String agentToken) {
        super();
        this.paymentRequestId = paymentRequestId;
        this.agentToken = agentToken;
        this.telemetry = new Telemetry(paymentRequestId);
    }

    /**
     * 
     * @return
     */
    public Date getLastInquiryTime() {
        return lastInquiryTime;
    }

    /**
     * 
     * @return
     */
    public int getInquiryCount() {
        return inquiryCount;
    }

    /**
     * 
     * @return
     */
    public int getCancelCount() {
        return cancelCount;
    }

    /**
     * Setter method for property <tt>inquiryCount</tt>.
     * 
     * @param inquiryCount value to be assigned to property inquiryCount
     */
    public void setInquiryCount(int inquiryCount) {
        this.inquiryCount = inquiryCount;
    }

    /**
     * Setter method for property <tt>cancelCount</tt>.
     * 
     * @param cancelCount value to be assigned to property cancelCount
     */
    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    /**
     * Setter method for property <tt>lastInquiryTime</tt>.
     * 
     * @param lastInquiryTime value to be assigned to property lastInquiryTime
     */
    public void setLastInquiryTime(Date lastInquiryTime) {
        this.lastInquiryTime = lastInquiryTime;
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
     * Getter method for property <tt>telemetry</tt>.
     * 
     * @return property value of telemetry
     */
    public Telemetry getTelemetry() {
        return telemetry;
    }

    /**
     * Setter method for property <tt>telemetry</tt>.
     * 
     * @param telemetry value to be assigned to property telemetry
     */
    public void setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * 
     * @return
     */
    public String toJson() {

        return new Gson().toJson(this);
    }

    /**
     * 
     * @param json
     * @return
     */
    public static PaymentContext fromJson(String json) {
        return new Gson().fromJson(json, PaymentContext.class);
    }

    /**
     * 
     * @return
     */
    public int getOrderCodeRequestCount() {
        return orderCodeRequestCount;
    }

    /**
     * 
     * @return
     */
    public void incrOrderCodeRequestCount() {
        orderCodeRequestCount++;
    }
}
