/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import java.util.concurrent.TimeUnit;

import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.job.Job;

/**
 * 
 * store & retrieve context info for a payment.
 * 
 * A solution with data persistent feature like Redis would be strongly recommended.
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContextCallback.java, v 0.1 2019年10月23日 下午3:51:48 guangling.zgl Exp $
 */
public interface PaymentContextCallback {

    /**
     * 
     * @param paymentRequestId
     * @param initial
     * @return
     */
    public PaymentContext loadContextByPaymentRequestIdOrDefault(String paymentRequestId,
                                                                 PaymentContext initial);

    public void saveContext(PaymentContext context);

    /**
     * 
     * Insert a new Job to a persistent repository like Redis or DB for future execution.
     * 
     * @param job
     */
    void insertNewJob(Job job);

    /**
     * Non-block mode required.
     * 
     * @param paymentRequestId
     * @param autoReleaseDelay
     * @param unit
     * @return true if lock acquired, else return false without blocking.
     */
    boolean lockJob(Job job, long autoReleaseDelay, TimeUnit unit);

    /**
     * 
     * @param job
     * @return
     */
    boolean releaseLock(Job job);

    /**
     * 
     * 
     * @param paymentRequestId
     * @return
     */
    boolean removeJob(Job job);

    /**
     * 
     * This method will be invoked periodically at a high frequency (at least 3 times in one seconds)
     * 
     * @return
     */
    Job[] listJobs();

}
