/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.callbacks.PaymentCancelCallback;
import com.alipay.ams.domain.callbacks.PaymentContextCallback;
import com.alipay.ams.domain.requests.PaymentCancelRequest;

/**
     * 
     * @author guangling.zgl
     * @version $Id: CancelJob.java, v 0.1 2019年10月28日 下午9:54:26 guangling.zgl Exp $
     */
public class CancelTask extends Task {

    private PaymentContextCallback paymentContextCallback;
    private AMSClient              client;
    private PaymentCancelCallback  paymentCancelCallback;

    /**
     * @param paymentContextCallback
     * @param paymentCancelCallback
     * @param paymentRequestId
     * @param client
     * @param job 
     */
    public CancelTask(PaymentContextCallback paymentContextCallback,
                      PaymentCancelCallback paymentCancelCallback, AMSClient client, Job job) {
        super(paymentContextCallback, client, job);
        this.paymentContextCallback = paymentContextCallback;
        this.paymentCancelCallback = paymentCancelCallback;
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

        if (paymentCancelCallback.needFurtherCancel(paymentContext, client.getSettings())) {

            client.getSettings().logger.warn("Running scheduled Cancel task: [%s]",
                paymentContext);

            paymentContext.setCancelCount(paymentContext.getCancelCount() + 1);
            paymentContextCallback.saveContext(paymentContext);

            client.execute(
                PaymentCancelRequest.byPaymentRequestId(client.getSettings(),
                    paymentContext.getPaymentRequestId(), paymentContext.getAgentToken()),
                paymentCancelCallback);

        } else {

            client.getSettings().logger.warn(
                "Cancel Job skipped (needFurtherInquiry=false). context [%s]", paymentContext);
        }

        return true;

    }

}