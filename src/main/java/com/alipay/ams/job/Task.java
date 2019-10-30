/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import java.util.concurrent.TimeUnit;

import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.LockSupport;
import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Task.java, v 0.1 2019年10月29日 上午12:02:32 guangling.zgl Exp $
 */
public abstract class Task implements Runnable {

    private JobSupport  jobSupport;
    private LockSupport lockSupport;
    private AMSSettings setting;
    protected Job       job;

    /**
     * @param jobSupport
     * @param lockSupport
     * @param setting
     * @param job
     */
    public Task(JobSupport jobSupport, LockSupport lockSupport, AMSSettings setting, Job job) {
        super();
        this.jobSupport = jobSupport;
        this.lockSupport = lockSupport;
        this.setting = setting;
        this.job = job;
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        if (lockSupport.lockJob(job, setting.lockAutoReleaseDelayInMilliSeconds,
            TimeUnit.MILLISECONDS)) {

            if (runTask()) {
                jobSupport.removeJob(job);
            }

            lockSupport.releaseLock(job);

        } else {
            setting.logger.warn("Lock failed for Job [%s]. Skipped. ", job);
        }

    }

    /**
     * 
     */
    protected abstract boolean runTask();

}
