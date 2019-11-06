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
            try {
                if (runTask()) {
                    jobSupport.removeJob(job);
                }
            } finally {
                lockSupport.releaseLock(job);
            }

        } else {
            setting.logger.warn("Lock failed for Job [%s]. Skipped. ", job);
        }

    }

    /**
     * 
     */
    protected abstract boolean runTask();

}
