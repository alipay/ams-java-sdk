/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.telemetry;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import com.alipay.ams.domain.PaymentStatusType;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Telemetry.java, v 0.1 2019年11月4日 下午3:44:06 guangling.zgl Exp $
 */
public class Telemetry implements Serializable {
    /**  */
    private static final long serialVersionUID = -1443362333176376924L;

    private Payment           payment          = new Payment();
    private Deque<Inquiry>    inquiries        = new ArrayDeque<Inquiry>();
    private Deque<Cancel>     cancels          = new ArrayDeque<Cancel>();
    private PaymentStatusType paymentStatusType;
    private boolean           isPaymentCancelResultUnknown;

    private long              notifySuccessReceivedMs;

    private String            paymentRequestId;

    /**
     * @param paymentRequestId
     */
    public Telemetry(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    /**
     * Getter method for property <tt>payment</tt>.
     * 
     * @return property value of payment
     */
    public Payment getPayment() {
        return payment;
    }

    public Cancel getLatestUnfinishedCancelOrInitOne() {

        if (cancels.peekFirst() == null || cancels.peekFirst().isFinished()) {

            Cancel cancel = new Cancel();
            cancels.offerFirst(cancel);
            return cancel;

        } else {
            return cancels.peekFirst();
        }
    }

    public Inquiry getLatestUnfinishedInquiryOrInitOne() {

        if (inquiries.peekFirst() == null || inquiries.peekFirst().isFinished()) {

            Inquiry inquiry = new Inquiry();
            inquiries.offerFirst(inquiry);
            return inquiry;

        } else {
            return inquiries.peekFirst();
        }
    }

    /**
     * 
     * @return
     */
    public boolean isReadyForTelemetry() {
        return this.isPaymentCancelResultUnknown || this.paymentStatusType != null;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s,%s,(%s,%s,%s,%s),(%s,%s,%s),(%s,%s,%s),(%s,%s)", paymentRequestId,
            defaultIfEmpty(paymentStatusType), defaultIfEmpty(payment.getResultStatusType()),
            defaultIfEmpty(payment.getRequestStartMs()),
            defaultIfEmpty(payment.getResponseReceivedMs()),
            defaultIfEmpty(payment.getIoExceptionOrHttpStatusNot200ReceivedMs()), inquiries.size(),
            defaultRequestStartMsIfEmpty(inquiries.peekFirst()),
            defaultRequestStartMsIfEmpty(inquiries.peekLast()), cancels.size(),
            defaultRequestStartMsIfEmpty(cancels.peekFirst()),
            defaultRequestStartMsIfEmpty(cancels.peekLast()),
            defaultIfEmpty(this.isPaymentCancelResultUnknown),
            defaultIfEmpty(this.notifySuccessReceivedMs));
    }

    private static String defaultIfEmpty(Object en) {
        return en == null ? "-" : en.toString();
    }

    private static String defaultRequestStartMsIfEmpty(Call call) {
        return call == null ? "-" : defaultIfEmpty(call.getRequestStartMs());
    }

    private static String defaultIfEmpty(boolean bol) {
        return bol ? "T" : "F";
    }

    private static String defaultIfEmpty(long l) {
        return l == 0 ? "-" : String.valueOf(l);
    }

    /**
     * 
     */
    public void setPaymentFailed() {
        paymentStatusType = PaymentStatusType.FAIL;
    }

    /**
     * 
     */
    public void setPaymentCanceled() {
        paymentStatusType = PaymentStatusType.CANCELLED;
    }

    /**
     * 
     */
    public void setPaymentSuccessed() {
        paymentStatusType = PaymentStatusType.SUCCESS;
    }

    /**
     * 
     */
    public void setPaymentCancelResultUnknown() {
        isPaymentCancelResultUnknown = true;
    }

    /**
     * 
     */
    public void setNotifiedPaymentSuccess() {

        paymentStatusType = PaymentStatusType.SUCCESS;
        this.notifySuccessReceivedMs = System.currentTimeMillis();
    }

    /**
     * Getter method for property <tt>inquiries</tt>.
     * 
     * @return property value of inquiries
     */
    public Deque<Inquiry> getInquiries() {
        return inquiries;
    }

    /**
     * Setter method for property <tt>inquiries</tt>.
     * 
     * @param inquiries value to be assigned to property inquiries
     */
    public void setInquiries(Deque<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }

    /**
     * Getter method for property <tt>cancels</tt>.
     * 
     * @return property value of cancels
     */
    public Deque<Cancel> getCancels() {
        return cancels;
    }

    /**
     * Setter method for property <tt>cancels</tt>.
     * 
     * @param cancels value to be assigned to property cancels
     */
    public void setCancels(Deque<Cancel> cancels) {
        this.cancels = cancels;
    }

    /**
     * Getter method for property <tt>paymentStatusType</tt>.
     * 
     * @return property value of paymentStatusType
     */
    public PaymentStatusType getPaymentStatusType() {
        return paymentStatusType;
    }

    /**
     * Setter method for property <tt>paymentStatusType</tt>.
     * 
     * @param paymentStatusType value to be assigned to property paymentStatusType
     */
    public void setPaymentStatusType(PaymentStatusType paymentStatusType) {
        this.paymentStatusType = paymentStatusType;
    }

    /**
     * Getter method for property <tt>isPaymentCancelResultUnknown</tt>.
     * 
     * @return property value of isPaymentCancelResultUnknown
     */
    public boolean isPaymentCancelResultUnknown() {
        return isPaymentCancelResultUnknown;
    }

    /**
     * Setter method for property <tt>isPaymentCancelResultUnknown</tt>.
     * 
     * @param isPaymentCancelResultUnknown value to be assigned to property isPaymentCancelResultUnknown
     */
    public void setPaymentCancelResultUnknown(boolean isPaymentCancelResultUnknown) {
        this.isPaymentCancelResultUnknown = isPaymentCancelResultUnknown;
    }

    /**
     * Getter method for property <tt>notifySuccessReceivedMs</tt>.
     * 
     * @return property value of notifySuccessReceivedMs
     */
    public long getNotifySuccessReceivedMs() {
        return notifySuccessReceivedMs;
    }

    /**
     * Setter method for property <tt>notifySuccessReceivedMs</tt>.
     * 
     * @param notifySuccessReceivedMs value to be assigned to property notifySuccessReceivedMs
     */
    public void setNotifySuccessReceivedMs(long notifySuccessReceivedMs) {
        this.notifySuccessReceivedMs = notifySuccessReceivedMs;
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
     * Setter method for property <tt>payment</tt>.
     * 
     * @param payment value to be assigned to property payment
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
