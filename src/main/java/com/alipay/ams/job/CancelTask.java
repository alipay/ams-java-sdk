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
package com.alipay.ams.job;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.PaymentCancelCallback;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.util.LockUtil;

/**
     * 
     * @author guangling.zgl
     * @version $Id: CancelJob.java, v 0.1 2019年10月28日 下午9:54:26 guangling.zgl Exp $
     */
public class CancelTask extends Task {

    private AMSClient             client;
    private PaymentCancelCallback paymentCancelCallback;

    /**
     * @param jobSupport
     * @param job
     * @param paymentCancelCallback
     * @param client
     */
    public CancelTask(JobSupport jobSupport, Job job, PaymentCancelCallback paymentCancelCallback,
                      AMSClient client) {

        super(jobSupport, paymentCancelCallback.getPaymentStatusUpdateCallback().getLockSupport(),
            client.getSettings(), job);
        this.client = client;
        this.paymentCancelCallback = paymentCancelCallback;
    }

    /** 
     * @see com.alipay.ams.job.Task#runTask()
     */
    @Override
    protected boolean runTask() {

        final PaymentContext paymentContext = paymentCancelCallback.getPaymentContextSupport()
            .loadContextByPaymentRequestIdOrDefault(job.getPaymentRequestId(), null);

        if (paymentContext == null) {

            client.getSettings().logger.warn(
                "PaymentContext not found and ignored. paymentRequestId=[%s]",
                job.getPaymentRequestId());
            return true;
        }

        if (paymentCancelCallback.needFurtherCancel(paymentContext, client.getSettings())) {

            client.getSettings().logger.warn("Running scheduled Cancel task: [%s]", paymentContext);

            boolean lockOK = LockUtil.executeWithLock(paymentCancelCallback
                .getPaymentStatusUpdateCallback().getLockSupport(), paymentContext
                .getPaymentRequestId(), new Runnable() {

                @Override
                public void run() {

                    if (paymentCancelCallback.getPaymentStatusUpdateCallback()
                        .isPaymentStatusSuccess(paymentContext.getPaymentRequestId())) {

                        client.getSettings().logger
                            .warn(
                                "Cancel Job skipped. Because Payment status now is already SUCCESS in partner system. context [%s]",
                                paymentContext);
                        return;
                    }

                    paymentContext.setCancelCount(paymentContext.getCancelCount() + 1);
                    paymentCancelCallback.getPaymentContextSupport().saveContext(paymentContext);

                    client.execute(
                        new PaymentCancelRequest(client.getSettings(), paymentContext
                            .getPaymentRequestId(), paymentContext.getAgentToken()),
                        paymentCancelCallback);

                }
            });

            if (!lockOK) {

                client.getSettings().logger.warn(
                    "Cancel Job skipped. Because Acquiring lock failed. context [%s]",
                    paymentContext);
            }

        } else {

            client.getSettings().logger.warn(
                "Cancel Job skipped (needFurtherCancel=false). context [%s]", paymentContext);
        }

        return true;

    }

}