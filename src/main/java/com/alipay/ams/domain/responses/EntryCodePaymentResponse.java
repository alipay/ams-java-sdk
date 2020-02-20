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
 * @version $Id: EntryCodePaymentResponse.java, v 0.1 2019年11月5日 下午9:57:40 guangling.zgl Exp $
 */
public class EntryCodePaymentResponse extends Response {

    private String paymentRequestId;
    private String paymentId;
    private Amount paymentAmount;
    private String paymentCreateTime;
    private String redirectUrl;

    /**
     * @param requestURI
     * @param settings
     * @param responseHeader
     * @param body
     */
    public EntryCodePaymentResponse(String requestURI, AMSSettings settings,
                                    ResponseHeader responseHeader, HashMap<String, Object> body) {
        super(requestURI, settings, responseHeader, body);
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap, com.alipay.ams.domain.ResponseHeader)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void initBody(HashMap<String, Object> body, ResponseHeader responseHeader) {

        this.paymentId = (String) body.get("paymentId");
        this.paymentRequestId = (String) body.get("paymentRequestId");

        this.paymentAmount = Amount.fromMap((Map<String, String>) body.get("paymentAmount"));

        Map<String, Object> redirectActionForm = (Map<String, Object>) body
            .get("redirectActionForm");
        this.redirectUrl = (String) redirectActionForm.get("redirectUrl");

        this.paymentCreateTime = (String) body.get("paymentCreateTime");

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
     * Getter method for property <tt>redirectUrl</tt>.
     * 
     * @return property value of redirectUrl
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Setter method for property <tt>redirectUrl</tt>.
     * 
     * @param redirectUrl value to be assigned to property redirectUrl
     */
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


}
