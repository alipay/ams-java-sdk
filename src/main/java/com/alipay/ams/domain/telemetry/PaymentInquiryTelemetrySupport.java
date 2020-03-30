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
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryTelemetrySupport.java, v 0.1 2020年3月27日 下午2:40:01 guangling.zgl Exp $
 */
public class PaymentInquiryTelemetrySupport
                                           extends
                                           TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> {

    /**
     * @param paymentContextSupport
     */
    public PaymentInquiryTelemetrySupport(PaymentContextSupport paymentContextSupport) {
        super(paymentContextSupport);
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getCurrentCall(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return paymentContext.getTelemetry().getLatestUnfinishedInquiryOrInitOne();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getPaymentRequestId(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getPaymentRequestId(PaymentInquiryRequest request) {

        if (StringUtil.isBlank(request.getPaymentRequestId())) {
            throw new IllegalStateException(
                "paymentRequestId required when using PaymentInquiryCallback. Try using SinglePaymentInquiryCallback instead.");
        }
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(PaymentInquiryRequest request) {
        return request.getAgentToken();
    }
}
