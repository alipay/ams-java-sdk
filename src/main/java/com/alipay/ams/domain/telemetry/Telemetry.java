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
package com.alipay.ams.domain.telemetry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private List<Inquiry>     inquiries        = new ArrayList<Inquiry>();
    private List<Cancel>      cancels          = new ArrayList<Cancel>();
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

        if (cancels.isEmpty() || cancels.get(0).isFinished()) {

            Cancel cancel = new Cancel();
            cancels.add(0, cancel);
            return cancel;

        } else {
            return cancels.get(0);
        }
    }

    public Inquiry getLatestUnfinishedInquiryOrInitOne() {

        if (inquiries.isEmpty() || inquiries.get(0).isFinished()) {

            Inquiry inquiry = new Inquiry();
            inquiries.add(0, inquiry);
            return inquiry;

        } else {
            return inquiries.get(0);
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
            defaultRequestStartMsIfEmpty(inquiries, 0),
            defaultRequestStartMsIfEmpty(inquiries, inquiries.size() - 1), cancels.size(),
            defaultRequestStartMsIfEmpty(cancels, 0),
            defaultRequestStartMsIfEmpty(cancels, cancels.size() - 1),
            defaultIfEmpty(this.isPaymentCancelResultUnknown),
            defaultIfEmpty(this.notifySuccessReceivedMs));
    }

    private static String defaultIfEmpty(Object en) {
        return en == null ? "-" : en.toString();
    }

    private static String defaultRequestStartMsIfEmpty(List<? extends Call> calls, int idx) {
        return calls.isEmpty() ? "-" : defaultIfEmpty(calls.get(idx).getRequestStartMs());
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
    public List<Inquiry> getInquiries() {
        return inquiries;
    }

    /**
     * Setter method for property <tt>inquiries</tt>.
     * 
     * @param inquiries value to be assigned to property inquiries
     */
    public void setInquiries(List<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }

    /**
     * Getter method for property <tt>cancels</tt>.
     * 
     * @return property value of cancels
     */
    public List<Cancel> getCancels() {
        return cancels;
    }

    /**
     * Setter method for property <tt>cancels</tt>.
     * 
     * @param cancels value to be assigned to property cancels
     */
    public void setCancels(List<Cancel> cancels) {
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
