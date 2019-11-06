/*
 * The MIT License
 * Copyright © 2019 Alipay
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
package com.alipay.ams.domain.responses;

import java.util.HashMap;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResponseHeader;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentRefundResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class PaymentRefundResponse extends Response {

    private String  paymentId;
    private String  refundId;
    private String  refundRequestId;
    private Amount  refundAmount;
    private String  refundTime;
    private boolean isAsyncRefund;

    /**
     * @param requestURI
     * @param settings
     * @param responseHeader
     */
    public PaymentRefundResponse(String requestURI, AMSSettings settings,
                                 ResponseHeader responseHeader, HashMap<String, Object> body) {
        super(requestURI, settings, responseHeader, body);
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap, com.alipay.ams.domain.ResponseHeader)
     */
    @Override
    protected void initBody(HashMap<String, Object> body, ResponseHeader responseHeader) {

        this.refundTime = (String) body.get("refundTime");
        this.paymentId = (String) body.get("paymentId");
        this.refundId = (String) body.get("refundId");
        this.refundRequestId = (String) body.get("refundRequestId");
        this.refundAmount = Amount.fromMap((Map<String, String>) body.get("refundAmount"));
        this.isAsyncRefund = "true".equals((String) body.get("isAsyncRefund"));
    }

    /** 
     * @see com.alipay.ams.domain.AMSMessage#getBizIdentifier()
     */
    @Override
    public String getBizIdentifier() {
        return refundRequestId;
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
     * Getter method for property <tt>refundRequestId</tt>.
     * 
     * @return property value of refundRequestId
     */
    public String getRefundRequestId() {
        return refundRequestId;
    }

    /**
     * Setter method for property <tt>refundRequestId</tt>.
     * 
     * @param refundRequestId value to be assigned to property refundRequestId
     */
    public void setRefundRequestId(String refundRequestId) {
        this.refundRequestId = refundRequestId;
    }

    /**
     * Getter method for property <tt>refundAmount</tt>.
     * 
     * @return property value of refundAmount
     */
    public Amount getRefundAmount() {
        return refundAmount;
    }

    /**
     * Setter method for property <tt>refundAmount</tt>.
     * 
     * @param refundAmount value to be assigned to property refundAmount
     */
    public void setRefundAmount(Amount refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * Getter method for property <tt>refundTime</tt>.
     * 
     * @return property value of refundTime
     */
    public String getRefundTime() {
        return refundTime;
    }

    /**
     * Setter method for property <tt>refundTime</tt>.
     * 
     * @param refundTime value to be assigned to property refundTime
     */
    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    /**
     * Getter method for property <tt>isAsyncRefund</tt>.
     * 
     * @return property value of isAsyncRefund
     */
    public boolean isAsyncRefund() {
        return isAsyncRefund;
    }

    /**
     * Setter method for property <tt>isAsyncRefund</tt>.
     * 
     * @param isAsyncRefund value to be assigned to property isAsyncRefund
     */
    public void setAsyncRefund(boolean isAsyncRefund) {
        this.isAsyncRefund = isAsyncRefund;
    }

    /**
     * Getter method for property <tt>refundId</tt>.
     * 
     * @return property value of refundId
     */
    public String getRefundId() {
        return refundId;
    }

    /**
     * Setter method for property <tt>refundId</tt>.
     * 
     * @param refundId value to be assigned to property refundId
     */
    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

}
