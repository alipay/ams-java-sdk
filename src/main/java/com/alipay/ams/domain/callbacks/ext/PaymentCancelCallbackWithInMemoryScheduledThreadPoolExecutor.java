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
import com.alipay.ams.domain.requests.PaymentCancelRequest;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelCallbackWithInMemoryScheduledThreadPoolExecutor.java, v 0.1 2019年10月28日 下午4:28:41 guangling.zgl Exp $
 */
public abstract class PaymentCancelCallbackWithInMemoryScheduledThreadPoolExecutor extends
                                                                                  PaymentCancelCallback {

    private ScheduledThreadPoolExecutor executor;

    /**
     * @param paymentCancelCallback
     * @param paymentContextCallback
     * @param executor
     */
    public PaymentCancelCallbackWithInMemoryScheduledThreadPoolExecutor(PaymentContextCallback paymentContextCallback,
                                                                        ScheduledThreadPoolExecutor executor) {
        super(paymentContextCallback);
        this.executor = executor;
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentCancelCallback#scheduleALaterCancel(com.alipay.ams.AMSClient, com.alipay.ams.domain.PaymentContext)
     */
    @Override
    protected void scheduleALaterCancel(final AMSClient client, final PaymentContext context) {

        final int cancelCount = context.getCancelCount();
        final PaymentCancelCallbackWithInMemoryScheduledThreadPoolExecutor self = this;

        executor.schedule(new Runnable() {

            @Override
            public void run() {

                //Reload task info in case someone else already had an update.
                PaymentContext paymentContext = getPaymentContextCallback()
                    .loadContextByPaymentRequestIdOrDefault(context.getPaymentRequestId(),
                        new PaymentContext(context.getPaymentRequestId(), context.getAgentToken()));

                if (needFurtherCancel(paymentContext, client.getSettings())) {

                    client.getSettings().logger.warn("Running scheduled Cancel task: [%s]",
                        paymentContext);

                    context.setCancelCount(cancelCount + 1);
                    getPaymentContextCallback().saveContext(context);

                    client.execute(
                        PaymentCancelRequest.byPaymentRequestId(client.getSettings(),
                            context.getPaymentRequestId(), context.getAgentToken()), self);

                } else {

                    client.getSettings().logger.warn(
                        "Cancel Job skipped new context [%s] VS old context[%s]", paymentContext,
                        context);
                }

            }
        }, client.getSettings().cancelInterval[cancelCount], TimeUnit.SECONDS);
    }

}
