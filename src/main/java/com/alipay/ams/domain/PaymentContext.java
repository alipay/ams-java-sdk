/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Date;

/**
 * 
 * Context info for a payment.
 * 
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContext.java, v 0.1 2019年10月23日 下午3:52:13 guangling.zgl Exp $
 */
public class PaymentContext {
    private int    inquiryCount;
    private int    cancelCount;
    private Date   lastInquiryTime;
    private String paymentRequestId;

    /**
     * @param paymentRequestId
     */
    public PaymentContext(String paymentRequestId) {
        super();
        this.paymentRequestId = paymentRequestId;
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

}
