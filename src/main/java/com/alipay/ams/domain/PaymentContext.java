/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
    private Date              lastInquiryTime;
    private String            paymentRequestId;
    private String            agentToken;

    /**
     * @param paymentRequestId
     * @param agentToken
     */
    public PaymentContext(String paymentRequestId, String agentToken) {
        super();
        this.paymentRequestId = paymentRequestId;
        this.agentToken = agentToken;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}