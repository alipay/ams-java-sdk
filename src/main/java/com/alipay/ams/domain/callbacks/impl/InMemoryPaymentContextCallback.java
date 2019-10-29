/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.callbacks.PaymentContextCallback;
import com.alipay.ams.job.Job;

/**
 * 
 * @author guangling.zgl
 * @version $Id: InMemoryPaymentContextCallback.java, v 0.1 2019年10月29日 上午12:27:16 guangling.zgl Exp $
 */
public class InMemoryPaymentContextCallback implements PaymentContextCallback {
    private final Object                value    = new Object();

    private Map<String, PaymentContext> repo     = new ConcurrentHashMap<String, PaymentContext>();
    private List<Job>                   jobRepo  = new ArrayList<Job>();
    private Map<Job, Object>            lockRepo = new ConcurrentHashMap<Job, Object>();

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#loadContextByPaymentRequestIdOrDefault(java.lang.String, com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public PaymentContext loadContextByPaymentRequestIdOrDefault(String paymentRequestId,
                                                                 PaymentContext initial) {
        if (repo.containsKey(paymentRequestId)) {

            return repo.get(paymentRequestId);

        } else {

            saveContext(initial);
            return initial;

        }
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#saveContext(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public void saveContext(PaymentContext context) {
        repo.put(context.getPaymentRequestId(), context);
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#insertNewJob(com.alipay.ams.job.Job)
     */
    @Override
    public void insertNewJob(Job job) {
        jobRepo.add(job);
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#lockJob(com.alipay.ams.job.Job, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean lockJob(Job job, long autoReleaseDelay, TimeUnit unit) {
        return lockRepo.putIfAbsent(job, value) == null;
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#releaseLock(com.alipay.ams.job.Job)
     */
    @Override
    public boolean releaseLock(Job job) {
        return lockRepo.remove(job) != null;
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#removeJob(com.alipay.ams.job.Job)
     */
    @Override
    public boolean removeJob(Job job) {
        return jobRepo.remove(job);
    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#listJobs()
     */
    @Override
    public Job[] listJobs() {
        return jobRepo.toArray(new Job[jobRepo.size()]);
    }

}
