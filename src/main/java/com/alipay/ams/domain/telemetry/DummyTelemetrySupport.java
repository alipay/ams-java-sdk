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

import com.alipay.ams.callbacks.TelemetrySupport;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.Request;
import com.alipay.ams.domain.Response;

/**
 * 
 * @author guangling.zgl
 * @version $Id: DummyTelemetrySupport.java, v 0.1 2020年3月27日 下午2:32:07 guangling.zgl Exp $
 */
public class DummyTelemetrySupport<R extends Request, P extends Response> extends
                                                                          TelemetrySupport<R, P> {

    /**
     * @param paymentContextSupport
     */
    public DummyTelemetrySupport() {
        super(null);
    }

    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return null;
    }

    @Override
    protected String getPaymentRequestId(R request) {
        return null;
    }

    @Override
    protected String getAgentToken(R request) {
        return null;
    }

}
