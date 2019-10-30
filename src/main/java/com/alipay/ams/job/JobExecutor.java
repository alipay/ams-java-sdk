/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.PaymentInquiryCallback;
import com.alipay.ams.job.Job.Type;

/**
 * 
 * @author guangling.zgl
 * @version $Id: JobExecutor.java, v 0.1 2019年10月28日 下午7:37:35 guangling.zgl Exp $
 */
public class JobExecutor {

    private JobSupport                  jobSupport;
    private PaymentInquiryCallback      paymentInquiryCallback;
    private AMSClient                   client;

    private ScheduledThreadPoolExecutor executor;

    private static AtomicBoolean        jobExecutorStarted = new AtomicBoolean(false);
    public final static JobExecutor     instance           = new JobExecutor();

    /**
     * 
     */
    private JobExecutor() {
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    public boolean startJobExecutor() {

        if (jobExecutorStarted.get()) {

            client.getSettings().logger.warn("jobExecutor has already been started. ");
            return false;

        } else {

            if (this.client == null || this.jobSupport == null
                || this.paymentInquiryCallback == null) {
                throw new IllegalStateException(
                    "JobExecutor not ready to start. paymentCancelCallback, paymentContextCallback and paymentInquiryCallback required.");
            }

            if (jobExecutorStarted.compareAndSet(false, true)) {

                this.executor = new ScheduledThreadPoolExecutor(
                    this.client.getSettings().corePoolSizeOfJobExecutor);

                this.executor.scheduleWithFixedDelay(new Runnable() {

                    @Override
                    public void run() {

                        try {

                            Job[] jobs = jobSupport.listJobs();

                            for (Job job : jobs) {

                                if (job.getTime() <= System.nanoTime()) {

                                    //Execute this job right now. in a non-blocking way by submitting it to a executor.
                                    executor.execute(job2Task(job));
                                }
                            }

                        } catch (Exception e) {
                            client.getSettings().logger.warn(
                                "Exception when doing listJobs: %s. Ignored.", e);
                        }

                    }
                }, 300, client.getSettings().jobListingDelayInMilliSeconds, TimeUnit.MILLISECONDS);

                client.getSettings().logger.info(
                    "jobExecutor started. scheduleWithFixedDelay=[%s] MILLISECONDS",
                    client.getSettings().jobListingDelayInMilliSeconds);

                return true;

            } else {
                client.getSettings().logger.warn("jobExecutor has already been started. ");
                return false;
            }

        }

    }

    private Runnable job2Task(Job job) {

        switch (job.getType()) {
            case INQUIRY:

                return new InquiryTask(jobSupport, job, paymentInquiryCallback, client);

            case CANCEL:

                return new CancelTask(jobSupport, job,
                    paymentInquiryCallback.getPaymentCancelCallback(), client);

            default:
                return null;
        }

    }

    /**
     * 
     * @param inquiryTask
     * @param delay
     * @param unit
     */
    public void scheduleInquiryJob(String paymentRequestId, int delay, TimeUnit unit) {

        jobSupport.insertNewJob(new Job(Type.INQUIRY, paymentRequestId, delay, unit));
    }

    /**
     * 
     * @param cancelTask
     * @param delay
     * @param unit
     */
    public void scheduleCancelJob(String paymentRequestId, int delay, TimeUnit unit) {

        jobSupport.insertNewJob(new Job(Type.CANCEL, paymentRequestId, delay, unit));
    }

    /**
     * 
     * @param runnable
     * @param delay
     * @param unit
     */
    public void schedule(Runnable command, int delay, TimeUnit unit) {
        this.executor.schedule(command, delay, unit);
    }

    /**
     * Getter method for property <tt>paymentInquiryCallback</tt>.
     * 
     * @return property value of paymentInquiryCallback
     */
    public PaymentInquiryCallback getPaymentInquiryCallback() {
        return paymentInquiryCallback;
    }

    /**
     * Setter method for property <tt>paymentInquiryCallback</tt>.
     * 
     * @param paymentInquiryCallback value to be assigned to property paymentInquiryCallback
     */
    public void setPaymentInquiryCallback(PaymentInquiryCallback paymentInquiryCallback) {
        this.paymentInquiryCallback = paymentInquiryCallback;
    }

    /**
     * Getter method for property <tt>client</tt>.
     * 
     * @return property value of client
     */
    public AMSClient getClient() {
        return client;
    }

    /**
     * Setter method for property <tt>client</tt>.
     * 
     * @param client value to be assigned to property client
     */
    public void setClient(AMSClient client) {
        this.client = client;
    }

    /**
     * Setter method for property <tt>jobSupport</tt>.
     * 
     * @param jobSupport value to be assigned to property jobSupport
     */
    public void setJobSupport(JobSupport jobSupport) {
        this.jobSupport = jobSupport;
    }

}
