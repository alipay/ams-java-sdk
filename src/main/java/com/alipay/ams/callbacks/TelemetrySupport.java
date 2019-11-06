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
package com.alipay.ams.callbacks;

import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.Request;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResultStatusType;
import com.alipay.ams.domain.telemetry.Call;

/**
 * 
 * @author guangling.zgl
 * @version $Id: TelemetrySupport.java, v 0.1 2019年11月4日 下午3:26:02 guangling.zgl Exp $
 */
public abstract class TelemetrySupport<R extends Request, P extends Response> {

    private PaymentContextSupport paymentContextSupport;

    /**
     * @param paymentContextSupport
     */
    public TelemetrySupport(PaymentContextSupport paymentContextSupport) {
        super();
        this.paymentContextSupport = paymentContextSupport;
    }

    /**
     * 
     * @param paymentContext 
     * @return
     */
    protected abstract Call getCurrentCall(PaymentContext paymentContext);

    protected abstract String getPaymentRequestId(R request);

    protected abstract String getAgentToken(R request);

    /**
     * 
     * @param request
     * @param requestHeader 
     */
    public void reportRequestStart(R request, RequestHeader requestHeader) {

        //1. setRequestStartMs for this payment.
        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(getPaymentRequestId(request),
                new PaymentContext(getPaymentRequestId(request), getAgentToken(request)));

        getCurrentCall(paymentContext).setRequestStartMs(System.currentTimeMillis());

        paymentContextSupport.saveContext(paymentContext);

        //2. Telemetry pending previous payments.
        if (request.getSettings().enableTelemetry) {

            String paymentRequestId = PaymentContextSupport.prevRequestTelemetry.poll();

            if (paymentRequestId != null) {

                PaymentContext prevPaymentContext = paymentContextSupport
                    .loadContextByPaymentRequestIdOrDefault(paymentRequestId, null);

                if (prevPaymentContext == null) {

                    request.getSettings().logger
                        .warn(
                            "PaymentContext not found and ignored when Telemetrying. paymentRequestId=[%s]",
                            paymentRequestId);
                    return;
                }

                if (!prevPaymentContext.getTelemetry().isReadyForTelemetry()) {

                    //Add it back at the tail of the queue since it is not ready yet.
                    PaymentContextSupport.prevRequestTelemetry.add(paymentRequestId);

                } else {

                    requestHeader.putExtraHeader("X-Telemetry", prevPaymentContext.getTelemetry()
                        .toString());
                }
            }
        }
    }

    /**
     * 
     * @param request
     */
    public void reportResponseReceived(R request) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(getPaymentRequestId(request),
                new PaymentContext(getPaymentRequestId(request), getAgentToken(request)));

        getCurrentCall(paymentContext).setResponseReceivedMs(System.currentTimeMillis());

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     */
    public void reportRequestIOExceptionOrHttpStatusNot200(R request) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(getPaymentRequestId(request),
                new PaymentContext(getPaymentRequestId(request), getAgentToken(request)));

        getCurrentCall(paymentContext).setIoExceptionOrHttpStatusNot200ReceivedMs(
            System.currentTimeMillis());

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     * @param resultStatusType
     */
    public void reportResultStatus(R request, ResultStatusType resultStatusType) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(getPaymentRequestId(request),
                new PaymentContext(getPaymentRequestId(request), getAgentToken(request)));

        getCurrentCall(paymentContext).setResultStatusType(resultStatusType);

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     * @param resultStatusType
     */
    public void reportPaymentS(String paymentRequestId) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(paymentRequestId, null);

        paymentContext.getTelemetry().setPaymentSuccessed();

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     * @param resultStatusType
     */
    public void reportPaymentF(String paymentRequestId) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(paymentRequestId, null);

        paymentContext.getTelemetry().setPaymentFailed();

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     * @param resultStatusType
     */
    public void reportPaymentCanceled(String paymentRequestId) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(paymentRequestId, null);

        paymentContext.getTelemetry().setPaymentCanceled();

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * 
     * @param request
     * @param resultStatusType
     */
    public void reportPaymentCancelResultUnknown(String paymentRequestId) {

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(paymentRequestId, null);

        paymentContext.getTelemetry().setPaymentCancelResultUnknown();

        paymentContextSupport.saveContext(paymentContext);
    }

    /**
     * Getter method for property <tt>paymentContextSupport</tt>.
     * 
     * @return property value of paymentContextSupport
     */
    public PaymentContextSupport getPaymentContextSupport() {
        return paymentContextSupport;
    }

}
