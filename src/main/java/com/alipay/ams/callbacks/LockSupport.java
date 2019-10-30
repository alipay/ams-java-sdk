/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.callbacks;

import java.util.concurrent.TimeUnit;

import com.alipay.ams.job.Job;

/**
 * 
 * @author guangling.zgl
 * @version $Id: LockSupport.java, v 0.1 2019年10月30日 下午5:49:43 guangling.zgl Exp $
 */
public interface LockSupport {

    /**
     * Non-block mode required.
     * 
     * @param paymentRequestId
     * @param autoReleaseDelay
     * @param unit
     * @return true if lock acquired, else return false without blocking.
     */
    public boolean lockJob(Job job, long autoReleaseDelay, TimeUnit unit);

    /**
     * 
     * @param job
     * @return
     */
    public boolean releaseLock(Job job);

    /** 
     * 
     * <p>A typical usage idiom for this method would be:
     *  <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}</pre>
     * 
     * @param paymentRequestId
     * @return {@code true} if the lock was acquired and
     *         {@code false} otherwise
     */
    public boolean tryLock4PaymentStatusUpdate(String paymentRequestId, long autoReleaseDelay,
                                               TimeUnit unit);

    /**
     * 
     * @param paymentRequestId
     */
    public boolean unlock4PaymentStatusUpdate(String paymentRequestId);

}