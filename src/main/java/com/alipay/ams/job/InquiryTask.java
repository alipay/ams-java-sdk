/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.PaymentInquiryCallback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;

/**
 * @author  guangling.zgl
 * @version  $Id: InquiryTask.java, v 0.1 2019年10月28日 下午7:11:09 guangling.zgl Exp $
 */
public class InquiryTask extends Task {

    private PaymentInquiryCallback paymentInquiryCallback;
    private AMSClient              client;

    /**
     * @param jobSupport
     * @param job
     * @param paymentInquiryCallback
     * @param client
     */
    public InquiryTask(JobSupport jobSupport, Job job,
                       PaymentInquiryCallback paymentInquiryCallback, AMSClient client) {
        super(jobSupport, paymentInquiryCallback.getPaymentCancelCallback()
            .getPaymentStatusUpdateCallback().getLockSupport(), client.getSettings(), job);
        this.paymentInquiryCallback = paymentInquiryCallback;
        this.client = client;
    }

    /** 
     * @see com.alipay.ams.job.Task#runTask()
     */
    @Override
    protected boolean runTask() {

        PaymentContext paymentContext = paymentInquiryCallback.getPaymentCancelCallback()
            .getPaymentContextSupport()
            .loadContextByPaymentRequestIdOrDefault(job.getPaymentRequestId(), null);

        if (paymentContext == null) {

            client.getSettings().logger.warn(
                "PaymentContext not found and ignored. paymentRequestId=[%s]",
                job.getPaymentRequestId());
            return true;
        }

        if (paymentInquiryCallback.needFurtherInquiry(paymentContext, client.getSettings())) {

            client.getSettings().logger
                .info("Running scheduled Inquiry task: [%s]", paymentContext);
            paymentContext.setInquiryCount(paymentContext.getInquiryCount() + 1);
            paymentInquiryCallback.getPaymentCancelCallback().getPaymentContextSupport()
                .saveContext(paymentContext);

            client.execute(
                PaymentInquiryRequest.byPaymentRequestId(client.getSettings(),
                    paymentContext.getPaymentRequestId(), paymentContext.getAgentToken()),
                paymentInquiryCallback);
            return true;

        } else {

            client.getSettings().logger.warn(
                "Inquiry Job skipped (needFurtherInquiry=false). context [%s]", paymentContext);
            return true;
        }

    }
}