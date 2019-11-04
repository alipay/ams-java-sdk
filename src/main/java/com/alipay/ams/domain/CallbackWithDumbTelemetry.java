/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
