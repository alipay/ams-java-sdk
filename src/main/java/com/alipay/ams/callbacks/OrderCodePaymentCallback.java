/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
import com.alipay.ams.domain.requests.OrderCodePaymentRequest;
import com.alipay.ams.domain.responses.OrderCodePaymentResponse;
import com.alipay.ams.domain.telemetry.Call;

/**
 * 
 * @author guangling.zgl
 * @version $Id: OrderCodePaymentCallback.java, v 0.1 2019年11月5日 下午10:10:34 guangling.zgl Exp $
 */
public abstract class OrderCodePaymentCallback
                                              extends
                                              Callback<OrderCodePaymentRequest, OrderCodePaymentResponse> {

    /**
     * @param paymentContextSupport
     */
    public OrderCodePaymentCallback(PaymentContextSupport paymentContextSupport) {
        super(paymentContextSupport);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client, OrderCodePaymentRequest request) {

        reportRequestIOExceptionOrHttpStatusNot200(request);

        retryOrFail(client, request, new ResponseResult("UNKNOWN", ResultStatusType.F,
            "IOException"));
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, OrderCodePaymentRequest request, int code) {

        reportRequestIOExceptionOrHttpStatusNot200(request);

        retryOrFail(client, request, new ResponseResult("UNKNOWN", ResultStatusType.F,
            "HttpStatusNot200"));
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, OrderCodePaymentRequest request,
                          ResponseResult responseResult) {

        reportPaymentF(request.getPaymentRequestId());

        onGetOrderCodeFailed(request, responseResult);
    }

    /**
     * 
     * @param request
     * @param responseResult
     */
    protected abstract void onGetOrderCodeFailed(OrderCodePaymentRequest request,
                                                 ResponseResult responseResult);

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onUstatus(AMSClient client, OrderCodePaymentRequest request,
                          ResponseResult responseResult) {

        client.getSettings().logger.warn("onUstatus");
        retryOrFail(client, request, responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, OrderCodePaymentRequest request) {

        //Actually order code generated success. Not a payment success.
        reportPaymentS(request.getPaymentRequestId());

        onOrderCodeResponse(new OrderCodePaymentResponse(requestURI, client.getSettings(),
            responseHeader, body));
    }

    /**
     * @param client 
     * 
     */
    private void retryOrFail(AMSClient client, OrderCodePaymentRequest request,
                             ResponseResult responseResultOfFail) {

        PaymentContext context = getPaymentContextSupport().loadContextByPaymentRequestIdOrDefault(
            request.getPaymentRequestId(),
            new PaymentContext(request.getPaymentRequestId(), request.getAgentToken()));

        if (needFurtherRetry(context, client.getSettings())) {

            context.incrOrderCodeRequestCount();
            getPaymentContextSupport().saveContext(context);

            client.execute(request, this);

        } else {

            onGetOrderCodeFailed(request, responseResultOfFail);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    private boolean needFurtherRetry(PaymentContext context, AMSSettings settings) {

        int count = context.getOrderCodeRequestCount();

        if (count + 1 > settings.maxOrderCodeRequestCount) {

            return false;
        }

        return true;
    }

    /**
     * 
     * @param orderCodeResponse
     */
    protected abstract void onOrderCodeResponse(OrderCodePaymentResponse orderCodeResponse);

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
    protected String getPaymentRequestId(OrderCodePaymentRequest request) {
        return request.getPaymentRequestId();
    }

    /** 
     * @see com.alipay.ams.callbacks.TelemetrySupport#getAgentToken(com.alipay.ams.domain.Request)
     */
    @Override
    protected String getAgentToken(OrderCodePaymentRequest request) {
        return request.getAgentToken();
    }

}
