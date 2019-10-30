/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.job;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.callbacks.PaymentCancelCallback;
import com.alipay.ams.domain.callbacks.PaymentContextCallback;
import com.alipay.ams.domain.callbacks.PaymentInquiryCallback;
import com.alipay.ams.job.Job.Type;

/**
 * 
 * @author guangling.zgl
 * @version $Id: JobExecutor.java, v 0.1 2019年10月28日 下午7:37:35 guangling.zgl Exp $
 */
public class JobExecutor {

    private PaymentContextCallback      paymentContextCallback;
    private PaymentInquiryCallback      paymentInquiryCallback;
    private PaymentCancelCallback       paymentCancelCallback;
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

            if (this.client == null || this.paymentCancelCallback == null
                || this.paymentContextCallback == null || this.paymentInquiryCallback == null) {
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

                            Job[] jobs = paymentContextCallback.listJobs();

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

                return new InquiryTask(paymentContextCallback, paymentInquiryCallback, client, job);

            case CANCEL:

                return new CancelTask(paymentContextCallback, paymentCancelCallback, client, job);

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

        paymentContextCallback.insertNewJob(new Job(Type.INQUIRY, paymentRequestId, delay, unit));
    }

    /**
     * 
     * @param cancelTask
     * @param delay
     * @param unit
     */
    public void scheduleCancelJob(String paymentRequestId, int delay, TimeUnit unit) {

        paymentContextCallback.insertNewJob(new Job(Type.CANCEL, paymentRequestId, delay, unit));
    }

    /**
     * Getter method for property <tt>paymentContextCallback</tt>.
     * 
     * @return property value of paymentContextCallback
     */
    public PaymentContextCallback getPaymentContextCallback() {
        return paymentContextCallback;
    }

    /**
     * Setter method for property <tt>paymentContextCallback</tt>.
     * 
     * @param paymentContextCallback value to be assigned to property paymentContextCallback
     */
    public void setPaymentContextCallback(PaymentContextCallback paymentContextCallback) {
        this.paymentContextCallback = paymentContextCallback;
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
     * Getter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @return property value of paymentCancelCallback
     */
    public PaymentCancelCallback getPaymentCancelCallback() {
        return paymentCancelCallback;
    }

    /**
     * Setter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @param paymentCancelCallback value to be assigned to property paymentCancelCallback
     */
    public void setPaymentCancelCallback(PaymentCancelCallback paymentCancelCallback) {
        this.paymentCancelCallback = paymentCancelCallback;
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
     * 
     * @param runnable
     * @param delay
     * @param unit
     */
    public void schedule(Runnable command, int delay, TimeUnit unit) {
        this.executor.schedule(command, delay, unit);
    }

}
