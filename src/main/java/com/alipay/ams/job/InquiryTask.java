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