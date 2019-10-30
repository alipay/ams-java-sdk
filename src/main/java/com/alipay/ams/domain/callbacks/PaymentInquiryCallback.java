/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.ResultStatusType;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.job.JobExecutor;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public class PaymentInquiryCallback extends Callback<PaymentInquiryRequest, PaymentInquiryResponse> {

    private PaymentCancelCallback       paymentCancelCallback;
    private PaymentContextCallback      paymentContextCallback;
    private PaymentStatusUpdateCallback paymentStatusUpdateCallback;

    /**
     * @param paymentCancelCallback
     * @param paymentContextCallback
     * @param paymentStatusUpdateCallback
     */
    public PaymentInquiryCallback(PaymentCancelCallback paymentCancelCallback,
                                  PaymentContextCallback paymentContextCallback,
                                  PaymentStatusUpdateCallback paymentStatusUpdateCallback) {
        this.paymentCancelCallback = paymentCancelCallback;
        this.paymentContextCallback = paymentContextCallback;
        this.paymentStatusUpdateCallback = paymentStatusUpdateCallback;
    }

    /**
     * 
     * A solution with data persistent feature like Redis would be strongly recommended.
     * 
     * @param amsSettings 
     * @param context 
     * 
     */
    public void scheduleALaterInquiry(final AMSClient client, final PaymentContext context) {

        int inquiryCount = context.getInquiryCount();

        JobExecutor.instance.scheduleInquiryJob(context.getPaymentRequestId(),
            client.getSettings().inquiryInterval[inquiryCount], TimeUnit.SECONDS);

    }

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onIOException(IOException e, AMSClient client,
                              PaymentInquiryRequest paymentInquiryRequest) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(int)
     */
    @Override
    public void onHttpStatusNot200(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                                   int code) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {

        if ("ORDER_NOT_EXIST".equals(responseResult.getResultCode())) {
            retryOrCancel(client, paymentInquiryRequest);
        } else {
            cancel(client, paymentInquiryRequest);
        }
    }

    /**
     * 
     */
    private void cancel(AMSClient client, PaymentInquiryRequest paymentInquiryRequest) {

        PaymentContext context = paymentContextCallback.loadContextByPaymentRequestIdOrDefault(
            paymentInquiryRequest.getPaymentRequestId(),
            new PaymentContext(paymentInquiryRequest.getPaymentRequestId(), paymentInquiryRequest
                .getAgentToken()));

        paymentCancelCallback.scheduleALaterCancel(client, context);
    }

    /**
     * @param client 
     * 
     */
    private void retryOrCancel(AMSClient client, PaymentInquiryRequest paymentInquiryRequest) {

        PaymentContext context = paymentContextCallback.loadContextByPaymentRequestIdOrDefault(
            paymentInquiryRequest.getPaymentRequestId(),
            new PaymentContext(paymentInquiryRequest.getPaymentRequestId(), paymentInquiryRequest
                .getAgentToken()));

        if (needFurtherInquiry(context, client.getSettings())) {

            //schedule a later Inquiry
            scheduleALaterInquiry(client, context);

        } else {
            cancel(client, paymentInquiryRequest);
        }

    }

    /**
     * 
     * @param context
     * @param settings
     * @return
     */
    public boolean needFurtherInquiry(PaymentContext context, AMSSettings settings) {
        int inquiryCount = context.getInquiryCount();

        if (inquiryCount + 1 > settings.maxInquiryCount) {
            return false;
        }

        return true;
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public void onUstatus(AMSClient client, PaymentInquiryRequest paymentInquiryRequest,
                          ResponseResult responseResult) {
        retryOrCancel(client, paymentInquiryRequest);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.cfg.AMSSettings, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentInquiryRequest paymentInquiryRequest) {
        PaymentInquiryResponse inquiryResponse = new PaymentInquiryResponse(requestURI,
            client.getSettings(), responseHeader, body);

        switch (inquiryResponse.getPaymentResultModel().getPaymentStatus()) {
            case SUCCESS:

                paymentStatusUpdateCallback.handlePaymentSuccess(inquiryResponse
                    .getPaymentResultModel());
                break;

            case FAIL:

                paymentStatusUpdateCallback.onPaymentFailed(inquiryResponse.getPaymentResultModel()
                    .getPaymentRequestId(), new ResponseResult("UNKNOWN", ResultStatusType.F,
                    "paymentStatus = FAIL in Inquiry response."));
                break;

            case CANCELLED:

                paymentStatusUpdateCallback.onPaymentCancelled(inquiryResponse
                    .getPaymentResultModel().getPaymentRequestId(), inquiryResponse
                    .getPaymentResultModel().getPaymentId(), null);//cancelTime not available from an Inquiry.
                break;

            case PROCESSING:

                retryOrCancel(client, paymentInquiryRequest);
                break;

            default:
                break;
        }
    }

    /**
     * Getter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @return property value of paymentCancelCallback
     */
    public PaymentCancelCallback getPaymentCancelCallback() {
        return paymentCancelCallback;
    }

    /**
     * Getter method for property <tt>paymentContextCallback</tt>.
     * 
     * @return property value of paymentContextCallback
     */
    public PaymentContextCallback getPaymentContextCallback() {
        return paymentContextCallback;
    }
}
