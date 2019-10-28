/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks.ext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.callbacks.PaymentCancelCallback;
import com.alipay.ams.domain.callbacks.PaymentContextCallback;
import com.alipay.ams.domain.callbacks.PaymentInquiryCallback;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryCallbackWithInMemoryScheduledThreadPoolExecutor.java, v 0.1 2019年10月28日 下午4:28:41 guangling.zgl Exp $
 */
public abstract class PaymentInquiryCallbackWithInMemoryScheduledThreadPoolExecutor extends
                                                                                   PaymentInquiryCallback {

    private ScheduledThreadPoolExecutor executor;

    /**
     * @param paymentCancelCallback
     * @param paymentContextCallback
     * @param executor
     */
    public PaymentInquiryCallbackWithInMemoryScheduledThreadPoolExecutor(PaymentCancelCallback paymentCancelCallback,
                                                                         PaymentContextCallback paymentContextCallback,
                                                                         ScheduledThreadPoolExecutor executor) {
        super(paymentCancelCallback, paymentContextCallback);
        this.executor = executor;
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentInquiryCallback#scheduleALaterInquiry(com.alipay.ams.AMSClient, com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public void scheduleALaterInquiry(final AMSClient client, final PaymentContext context) {

        final int inquiryCount = context.getInquiryCount();
        final PaymentInquiryCallbackWithInMemoryScheduledThreadPoolExecutor self = this;

        executor.schedule(new Runnable() {

            @Override
            public void run() {

                //Reload task info in case someone else already had an update.
                PaymentContext paymentContext = getPaymentContextCallback()
                    .loadContextByPaymentRequestIdOrDefault(context.getPaymentRequestId(),
                        new PaymentContext(context.getPaymentRequestId(), context.getAgentToken()));

                if (needFurtherInquiry(paymentContext, client.getSettings())) {

                    client.getSettings().logger.info("Running scheduled Inquiry task: [%s]",
                        paymentContext);

                    context.setInquiryCount(inquiryCount + 1);
                    getPaymentContextCallback().saveContext(context);

                    client.execute(
                        PaymentInquiryRequest.byPaymentRequestId(client.getSettings(),
                            context.getPaymentRequestId(), context.getAgentToken()), self);

                } else {

                    client.getSettings().logger.warn(
                        "Inquiry Job skipped new context [%s] VS old context[%s]", paymentContext,
                        context);
                }

            }
        }, client.getSettings().inquiryInterval[inquiryCount], TimeUnit.SECONDS);
    }
}
