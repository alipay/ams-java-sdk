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
package com.alipay.ams.callbacks;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.alipay.ams.domain.PaymentContext;

/**
 * 
 * store & retrieve context info for a payment.
 * 
 * A solution with data persistent feature like Redis would be strongly recommended.
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContextSupport.java, v 0.1 2019年10月23日 下午3:51:48 guangling.zgl Exp $
 */
public interface PaymentContextSupport {
    static final int                     MAX_REQUEST_TELEMETRY_BUFFER_SIZE = 100;
    static ConcurrentLinkedQueue<String> prevRequestTelemetry              = new ConcurrentLinkedQueue<String>();

    /**
     * 
     * @param paymentRequestId
     * @param initial
     * @return
     */
    public PaymentContext loadContextByPaymentRequestIdOrDefault(String paymentRequestId,
                                                                 PaymentContext initial);

    public void saveContext(PaymentContext context);

}
