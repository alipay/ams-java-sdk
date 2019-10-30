/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.PaymentContextCallback;
import com.alipay.ams.callbacks.PaymentInquiryCallback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;

/**
 * @author  guangling.zgl
 * @version  $Id: InquiryTask.java, v 0.1 2019年10月28日 下午7:11:09 guangling.zgl Exp $
 */
public class InquiryTask extends Task {

    private PaymentContextCallback paymentContextCallback;
    private PaymentInquiryCallback paymentInquiryCallback;
    private AMSClient              client;

    /**
     * @param paymentContextCallback
     * @param paymentInquiryCallback
     * @param paymentContext
     * @param client
     * @param job 
     */
    public InquiryTask(PaymentContextCallback paymentContextCallback,
                       PaymentInquiryCallback paymentInquiryCallback, AMSClient client, Job job) {
        super(paymentContextCallback, client, job);
        this.paymentContextCallback = paymentContextCallback;
        this.paymentInquiryCallback = paymentInquiryCallback;
        this.client = client;
    }

    /** 
     * @see com.alipay.ams.job.Task#runTask()
     */
    @Override
    protected boolean runTask() {

        PaymentContext paymentContext = paymentContextCallback
            .loadContextByPaymentRequestIdOrDefault(job.getPaymentRequestId(), null);

        if (paymentContext == null) {

            client.getSettings().logger.warn(
                "PaymentContext not found and ignored. paymentRequestId=[%s]",
                job.getPaymentRequestId());
            return true;
        }

        if (paymentInquiryCallback.needFurtherInquiry(paymentContext, client.getSettings())) {

            client.getSettings().logger.info("Running scheduled Inquiry task: [%s]",
                paymentContext);
            paymentContext.setInquiryCount(paymentContext.getInquiryCount() + 1);
            paymentContextCallback.saveContext(paymentContext);

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