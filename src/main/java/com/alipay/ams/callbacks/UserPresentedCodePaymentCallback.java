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

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.UserPresentedCodePaymentResponse;
import com.alipay.ams.domain.telemetry.Call;

/**
 * 
 * @author guangling.zgl
 * @version $Id: UserPresentedCodePaymentCallback.java, v 0.1 2019年8月26日 下午6:27:20 guangling.zgl Exp $
 */
public class UserPresentedCodePaymentCallback
                                             extends
                                             Callback<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> {

    private PaymentInquiryCallback paymentInquiryCallback;

    /**
     * @param paymentInquiryCallback
     */
    public UserPresentedCodePaymentCallback(PaymentInquiryCallback paymentInquiryCallback) {
        super(paymentInquiryCallback.getPaymentCancelCallback().getPaymentContextSupport());
        this.paymentInquiryCallback = paymentInquiryCallback;
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client,
                              UserPresentedCodePaymentRequest paymentRequest) {

        reportRequestIOExceptionOrHttpStatusNot200(paymentRequest);

        //Initiate a Inquiry
        client.execute(
            PaymentInquiryRequest.byPaymentRequestId(client.getSettings(),
                paymentRequest.getPaymentRequestId(), paymentRequest.getAgentToken()),
            paymentInquiryCallback);

    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, UserPresentedCodePaymentRequest request,
                                   int code) {

        reportRequestIOExceptionOrHttpStatusNot200(request);

        //Initiate a Inquiry
        client.execute(
            PaymentInquiryRequest.byPaymentRequestId(client.getSettings(),
                request.getPaymentRequestId(), request.getAgentToken()), paymentInquiryCallback);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, UserPresentedCodePaymentRequest request,
                          ResponseResult responseResult) {

        reportPaymentF(request.getPaymentRequestId());

        paymentInquiryCallback.getPaymentCancelCallback().getPaymentStatusUpdateCallback()
            .onPaymentFailed(request.getPaymentRequestId(), responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onUstatus(AMSClient client, UserPresentedCodePaymentRequest paymentRequest,
                          ResponseResult responseResult) {

        //Initiate an Inquiry
        client.execute(
            PaymentInquiryRequest.byPaymentRequestId(client.getSettings(),
                paymentRequest.getPaymentRequestId(), paymentRequest.getAgentToken()),
            paymentInquiryCallback);

    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, UserPresentedCodePaymentRequest request) {

        reportPaymentS(request.getPaymentRequestId());

        UserPresentedCodePaymentResponse paymentResponse = new UserPresentedCodePaymentResponse(
            client.getSettings(), requestURI, responseHeader, body);

        paymentInquiryCallback.getPaymentCancelCallback().getPaymentStatusUpdateCallback()
            .handlePaymentSuccess(paymentResponse.getPaymentResultModel());

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
    protected String getPaymentRequestId(UserPresentedCodePaymentRequest request) {
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(UserPresentedCodePaymentRequest request) {
        return request.getAgentToken();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#reportRequestStart(com.alipay.ams.domain.Request, com.alipay.ams.domain.RequestHeader)
     */
    @Override
    public void reportRequestStart(UserPresentedCodePaymentRequest request,
                                   RequestHeader requestHeader) {

        super.reportRequestStart(request, requestHeader);

        //Record for later Telemetry only on a new UserPresentedCodePaymentRequest
        if (request.getSettings().enableTelemetry
            && PaymentContextSupport.prevRequestTelemetry.size() < PaymentContextSupport.MAX_REQUEST_TELEMETRY_BUFFER_SIZE) {

            PaymentContextSupport.prevRequestTelemetry.add(request.getPaymentRequestId());

        } else if (request.getSettings().enableTelemetry) {

            request.getSettings().logger.warn(
                "Pending prevRequestTelemetry exceeded MAX buffer size: %s >= %s ",
                PaymentContextSupport.prevRequestTelemetry.size(),
                PaymentContextSupport.MAX_REQUEST_TELEMETRY_BUFFER_SIZE);
        }

    }
}
