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