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

import com.alipay.ams.callbacks.PaymentContextSupport;
import com.alipay.ams.callbacks.TelemetrySupport;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.requests.EntryCodePaymentRequest;
import com.alipay.ams.domain.responses.EntryCodePaymentResponse;

/**
 * 
 * @author guangling.zgl
 * @version $Id: EntryCodePaymentTelemetrySupport.java, v 0.1 2020年3月27日 下午2:36:04 guangling.zgl Exp $
 */
public class EntryCodePaymentTelemetrySupport
                                             extends
                                             TelemetrySupport<EntryCodePaymentRequest, EntryCodePaymentResponse> {

    /**
     * @param paymentContextSupport
     */
    public EntryCodePaymentTelemetrySupport(PaymentContextSupport paymentContextSupport) {
        super(paymentContextSupport);
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getCurrentCall(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return paymentContext.getTelemetry().getPayment();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getPaymentRequestId(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getPaymentRequestId(EntryCodePaymentRequest request) {
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(EntryCodePaymentRequest request) {
        return request.getAgentToken();
    }

}
