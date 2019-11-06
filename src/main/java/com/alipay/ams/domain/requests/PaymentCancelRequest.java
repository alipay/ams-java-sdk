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
package com.alipay.ams.domain.requests;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Body;
import com.alipay.ams.domain.Request;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelRequest.java, v 0.1 2019年10月18日 下午5:58:58 guangling.zgl Exp $
 */
public class PaymentCancelRequest extends Request {

    private String paymentRequestId;

    /**
     * @param settings
     * @param paymentRequestId
     */
    public PaymentCancelRequest(AMSSettings settings, String paymentRequestId) {

        this(settings, paymentRequestId, null);

    }

    /**
     * @param settings
     * @param paymentRequestId
     * @param agentToken
     */
    public PaymentCancelRequest(AMSSettings settings, String paymentRequestId, String agentToken) {

        super("/ams/api/v1/payments/cancel", settings);
        super.setAgentToken(agentToken);
        this.paymentRequestId = paymentRequestId;

    }

    /** 
     * @see com.alipay.ams.domain.Request#buildBody()
     */
    @Override
    public Body buildBody() {

        Body body = new Body();
        body.put("paymentRequestId", paymentRequestId);

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
     * @see com.alipay.ams.domain.Request#extValidate()
     */
    @Override
    protected boolean extValidate() {
        return true;
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
