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

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.ResultStatusType;
import com.alipay.ams.domain.requests.EntryCodePaymentRequest;
import com.alipay.ams.domain.responses.EntryCodePaymentResponse;
import com.alipay.ams.domain.telemetry.EntryCodePaymentTelemetrySupport;

/**
 * 
 * @author guangling.zgl
 * @version $Id: EntryCodePaymentCallback.java, v 0.1 2019年11月5日 下午10:10:34 guangling.zgl Exp $
 */
public abstract class EntryCodePaymentCallback
                                              extends
                                              Callback<EntryCodePaymentRequest, EntryCodePaymentResponse> {

    private PaymentContextSupport paymentContextSupport;

    /**
     * @param paymentContextSupport
     */
    public EntryCodePaymentCallback(PaymentContextSupport paymentContextSupport) {
        super(new EntryCodePaymentTelemetrySupport(paymentContextSupport));
        this.paymentContextSupport = paymentContextSupport;
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client, EntryCodePaymentRequest request) {

        getTelemetrySupport().reportRequestIOExceptionOrHttpStatusNot200(request);

        retryOrFail(client, request, new ResponseResult("UNKNOWN", ResultStatusType.F,
            "IOException"));
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, EntryCodePaymentRequest request, int code) {

        getTelemetrySupport().reportRequestIOExceptionOrHttpStatusNot200(request);

        retryOrFail(client, request, new ResponseResult("UNKNOWN", ResultStatusType.F,
            "HttpStatusNot200"));
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, EntryCodePaymentRequest request,
                          ResponseResult responseResult) {

        getTelemetrySupport().reportPaymentF(request.getPaymentRequestId());

        onGetEntryCodeFailed(request, responseResult);
    }

    /**
     * 
     * @param request
     * @param responseResult
     */
    protected abstract void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                 ResponseResult responseResult);

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onUstatus(AMSClient client, EntryCodePaymentRequest request,
                          ResponseResult responseResult) {

        client.getSettings().logger.warn("onUstatus");
        retryOrFail(client, request, responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, EntryCodePaymentRequest request) {

        //Actually entry code generated success. Not a payment success.
        getTelemetrySupport().reportPaymentS(request.getPaymentRequestId());

        onEntryCodeResponse(new EntryCodePaymentResponse(requestURI, client.getSettings(),
            responseHeader, body));
    }

    /**
     * @param client 
     * 
     */
    private void retryOrFail(AMSClient client, EntryCodePaymentRequest request,
                             ResponseResult responseResultOfFail) {

        PaymentContext context = paymentContextSupport.loadContextByPaymentRequestIdOrDefault(
            request.getPaymentRequestId(), new PaymentContext(request.getPaymentRequestId(),
                request.getAgentToken()));

        if (needFurtherRetry(context, client.getSettings())) {

            context.incrEntryCodeRequestCount();
            paymentContextSupport.saveContext(context);

            client.execute(request, this);

        } else {

            onGetEntryCodeFailed(request, responseResultOfFail);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    private boolean needFurtherRetry(PaymentContext context, AMSSettings settings) {

        int count = context.getEntryCodeRequestCount();

        if (count + 1 > settings.maxEntryCodeRequestCount) {

            return false;
        }

        return true;
    }

    /**
     * 
     * @param entryCodeResponse
     */
    protected abstract void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse);

}
