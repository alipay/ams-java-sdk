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
package com.alipay.ams.domain;

import com.alipay.ams.domain.telemetry.Call;

/**
 * 
 * @author guangling.zgl
 * @version $Id: CallbackWithDumbTelemetry.java, v 0.1 2019年11月4日 下午5:35:41 guangling.zgl Exp $
 */
public abstract class CallbackWithDumbTelemetry<R extends Request, P extends Response>
                                                                                       extends
                                                                                       Callback<R, P> {

    /**
     * @param paymentContextSupport
     */
    public CallbackWithDumbTelemetry() {
        super(null);
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getCurrentCall(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected Call getCurrentCall(PaymentContext paymentContext) {
        return null;
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getPaymentRequestId(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getPaymentRequestId(Request request) {
        return null;
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(Request request) {
        return null;
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#reportRequestIOExceptionOrHttpStatusNot200(com.alipay.ams.domain.Request)
     */
    @Override
    public void reportRequestIOExceptionOrHttpStatusNot200(R request) {
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#reportRequestStart(com.alipay.ams.domain.Request, com.alipay.ams.domain.RequestHeader)
     */
    @Override
    public void reportRequestStart(R request, RequestHeader requestHeader) {
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#reportResponseReceived(com.alipay.ams.domain.Request)
     */
    @Override
    public void reportResponseReceived(R request) {
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#reportResultStatus(com.alipay.ams.domain.Request, com.alipay.ams.domain.ResultStatusType)
     */
    @Override
    public void reportResultStatus(R request, ResultStatusType resultStatusType) {
    }

}
