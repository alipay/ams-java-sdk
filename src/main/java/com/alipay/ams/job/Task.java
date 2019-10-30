/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import java.util.concurrent.TimeUnit;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.PaymentContextCallback;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Task.java, v 0.1 2019年10月29日 上午12:02:32 guangling.zgl Exp $
 */
public abstract class Task implements Runnable {

    private PaymentContextCallback paymentContextCallback;
    private AMSClient              client;
    protected Job                  job;

    /**
     * @param paymentContextCallback
     * @param client
     * @param job
     */
    public Task(PaymentContextCallback paymentContextCallback, AMSClient client, Job job) {
        this.paymentContextCallback = paymentContextCallback;
        this.client = client;
        this.job = job;
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        if (paymentContextCallback.lockJob(job,
            client.getSettings().lockAutoReleaseDelayInMilliSeconds, TimeUnit.MILLISECONDS)) {

            if (runTask()) {
                paymentContextCallback.removeJob(job);
            }

            paymentContextCallback.releaseLock(job);

        } else {
            client.getSettings().logger.warn("Lock failed for Job [%s]. Skipped. ", job);
        }

    }

    /**
     * 
     */
    protected abstract boolean runTask();

}
