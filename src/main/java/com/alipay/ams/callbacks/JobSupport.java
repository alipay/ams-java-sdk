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

import com.alipay.ams.job.Job;

/**
 * 
 * @author guangling.zgl
 * @version $Id: JobSupport.java, v 0.1 2019年10月30日 下午5:51:24 guangling.zgl Exp $
 */
public interface JobSupport {

    /**
     * 
     * Insert a new Job to a persistent repository like Redis or DB for future execution.
     * 
     * @param job
     */
    public void insertNewJob(Job job);

    /**
     * 
     * 
     * @param paymentRequestId
     * @return
     */
    public boolean removeJob(Job job);

    /**
     * 
     * This method will be invoked periodically at a high frequency (at least 3 times in one seconds)
     * 
     * @return
     */
    public Job[] listJobs();

}