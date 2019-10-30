/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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