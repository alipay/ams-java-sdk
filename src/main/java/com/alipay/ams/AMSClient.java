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
package com.alipay.ams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.NotifyCallback;
import com.alipay.ams.domain.NotifyRequestHeader;
import com.alipay.ams.domain.Request;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.responses.NotifyResponse;
import com.alipay.ams.domain.responses.NotifyResponseImpl;
import com.alipay.ams.util.HeaderUtil;
import com.alipay.ams.util.SignatureUtil;
import com.google.gson.Gson;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMSClient.java, v 0.1 2019年8月26日 下午6:27:58 guangling.zgl Exp $
 */
public abstract class AMSClient {

    private AMSSettings settings;

    /**
     * 
     */
    public AMSClient(AMSSettings settings) {
        this.settings = settings;
    }

    /**
     * Getter method for property <tt>settings</tt>.
     * 
     * @return property value of settings
     */
    public AMSSettings getSettings() {
        return settings;
    }

    /**
     * 
     * @param request
     * @param callback
     */
    public abstract <R extends Request, P extends Response> void execute(R request,
                                                                         Callback<R, P> callback);

    public NotifyResponse onNotify(String requestURI, Map<String, String> notifyRequestheaders,
                                   byte[] bodyContent, NotifyCallback notifyCallback) {

        //1. Load response headers
        NotifyRequestHeader notifyRequestHeader = new NotifyRequestHeader(notifyRequestheaders);

        //2. Load http request body content
        String requestBody = null;

        try {

            notifyRequestHeader.validate();

            try {
                requestBody = new String(bodyContent, HeaderUtil.getCharset(notifyRequestHeader));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            //3. Signature verification.
            if (settings.isDevMode()
                || SignatureUtil.verify(requestURI, notifyRequestHeader.getClientId(),
                    notifyRequestHeader.getRequestTime(), settings.alipayPublicKey, requestBody,
                    notifyRequestHeader.getSignature().getSignature())) {

                processNotifyBody(notifyCallback, notifyRequestHeader, requestBody);

                return new NotifyResponseImpl(settings, requestURI, NotifyResponseImpl.S,
                    notifyRequestHeader.getAgentToken());

            } else {

                notifyCallback.onNotifySignatureVerifyFailed(notifyRequestHeader, requestBody);

                return new NotifyResponseImpl(settings, requestURI, NotifyResponseImpl.U,
                    notifyRequestHeader.getAgentToken());
            }

        } catch (Exception e) {

            settings.logger.warn("Exception[%s - %s] occured when onNotify， requestBody=[%s]", e
                .getClass().getName(), e.getMessage(), requestBody);

            return new NotifyResponseImpl(settings, requestURI, NotifyResponseImpl.U,
                notifyRequestHeader.getAgentToken());
        }

    }

    /**
     * 
     * @param callback
     * @param statusCode
     */
    protected <R extends Request, P extends Response> void onHttpStatusNot200(Callback<R, P> callback,
                                                                              R request,
                                                                              int statusCode) {
        callback.onHttpStatusNot200(this, request, statusCode);
    }

    /**
     * 
     * @param callback
     * @param e
     */
    protected <R extends Request, P extends Response> void onIOException(Callback<R, P> callback,
                                                                         R request, IOException e) {
        callback.onIOException(e, this, request);
    }

    /**
     * 
     * @param request
     * @param callback
     * @param bodyByteArray
     * @param headerMap
     */
    protected <R extends Request, P extends Response> void onHttpStatus200(R request,
                                                                           Callback<R, P> callback,
                                                                           byte[] bodyByteArray,
                                                                           Map<String, String> headerMap) {
        //1. Load response headers
        ResponseHeader responseHeader = new ResponseHeader(headerMap);

        //2. Load http response body content
        String responseBody;
        try {
            responseBody = new String(bodyByteArray, HeaderUtil.getCharset(responseHeader));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        getSettings().logger.debug("Response[%s][%s]: body->[%s], header->[%s]",
            request.getBizIdentifier(), request.getSettings().gatewayUrl + request.getRequestURI(),
            responseBody, responseHeader);

        //3. Signature verification.
        if (settings.isDevMode()
            || responseHeader.validate()
            && SignatureUtil.verify(request.getRequestURI(), responseHeader.getClientId(),
                responseHeader.getResponseTime(), request.getSettings().alipayPublicKey,
                responseBody, responseHeader.getSignature().getSignature())) {

            processResponseBody(request, callback, responseHeader, responseBody);

        } else {

            callback.onSignatureVerifyFailed(request, responseHeader, responseBody);
        }

    }

    /**
     * 
     * @param request
     * @param callback
     * @param responseHeader
     * @param responseBody
     */
    private <R extends Request, P extends Response> void processResponseBody(R request,
                                                                             Callback<R, P> callback,
                                                                             ResponseHeader responseHeader,
                                                                             String responseBody) {
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> body = gson.fromJson(responseBody, HashMap.class);

        @SuppressWarnings("unchecked")
        ResponseResult responseResult = ResponseResult.fromMap((Map<String, String>) body
            .get("result"));

        callback.reportResultStatus(request, responseResult.getResultStatus());

        switch (responseResult.getResultStatus()) {
            case F:
                callback.onFstatus(this, request, responseResult);

                break;
            case U:
                callback.onUstatus(this, request, responseResult);

                break;
            case S:
                callback.onSstatus(this, request.getRequestURI(), responseHeader, body, request);

                break;

            default:
                break;
        }
    }

    /**
     * 
     * @param notifyCallback
     * @param notifyRequestHeader
     * @param requestBody
     */
    private void processNotifyBody(NotifyCallback notifyCallback,
                                   NotifyRequestHeader notifyRequestHeader, String requestBody) {
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> body = gson.fromJson(requestBody, HashMap.class);

        // No `result` element in the body, treat it as a non-payment notify.
        if (body.get("result") == null) {
            notifyCallback.onNonPaymentNotify(settings, notifyRequestHeader, body);
            return;
        }

        @SuppressWarnings("unchecked")
        ResponseResult notifyResult = ResponseResult.fromMap((Map<String, String>) body
            .get("result"));

        switch (notifyResult.getResultStatus()) {
            case F:
                notifyCallback.onFstatus(notifyResult);

                break;
            case U:
                notifyCallback.onUstatus(notifyResult);

                break;
            case S:
                notifyCallback.onSstatus(settings, notifyRequestHeader, body);

                break;

            default:
                break;
        }
    }
}
