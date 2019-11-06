/*
 * The MIT License
 * Copyright © 2019 Alipay
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
package amsSdkDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.LockSupport;
import com.alipay.ams.callbacks.PaymentContextSupport;
import com.alipay.ams.callbacks.PaymentStatusUpdateCallback;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.job.Job;

/**
 * ALL in ONE reference implementation
 * 
 * @author guangling.zgl
 * @version $Id: InMemoryCallbackImpl.java, v 0.1 2019年10月29日 上午12:27:16 guangling.zgl Exp $
 */
public class InMemoryCallbackImpl extends PaymentStatusUpdateCallback implements
                                                                     PaymentContextSupport,
                                                                     LockSupport, JobSupport {

    private final Object                value                 = new Object();

    private Map<String, PaymentContext> repo                  = new ConcurrentHashMap<String, PaymentContext>();
    private List<Job>                   jobRepo               = new ArrayList<Job>();
    private Map<Job, Object>            jobLockRepo           = new ConcurrentHashMap<Job, Object>();
    private Map<String, Object>         paymentStatusLockRepo = new ConcurrentHashMap<String, Object>();

    /**
     * @param setting
     * @param lockSupport
     */
    public InMemoryCallbackImpl(AMSSettings setting) {
        super(setting, null);
    }

    /**
     * @param setting
     * @param lockSupport
     */
    public InMemoryCallbackImpl(AMSSettings setting, LockSupport lockSupport) {
        super(setting, lockSupport);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#loadContextByPaymentRequestIdOrDefault(java.lang.String, com.alipay.ams.domain.PaymentContext)
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
     * @see com.alipay.ams.callbacks.PaymentContextCallback#saveContext(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public void saveContext(PaymentContext context) {
        repo.put(context.getPaymentRequestId(), context);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#insertNewJob(com.alipay.ams.job.Job)
     */
    @Override
    public void insertNewJob(Job job) {
        jobRepo.add(job);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#lockJob(com.alipay.ams.job.Job, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean lockJob(Job job, long autoReleaseDelay, TimeUnit unit) {
        return jobLockRepo.putIfAbsent(job, value) == null;
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#releaseLock(com.alipay.ams.job.Job)
     */
    @Override
    public boolean releaseLock(Job job) {
        return jobLockRepo.remove(job) != null;
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#removeJob(com.alipay.ams.job.Job)
     */
    @Override
    public boolean removeJob(Job job) {
        return jobRepo.remove(job);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#listJobs()
     */
    @Override
    public Job[] listJobs() {
        return jobRepo.toArray(new Job[jobRepo.size()]);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#tryLock4PaymentStatusUpdate(java.lang.String, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean tryLock4PaymentStatusUpdate(String paymentRequestId, long autoReleaseDelay,
                                               TimeUnit unit) {
        return paymentStatusLockRepo.putIfAbsent(paymentRequestId, value) == null;
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#unlock4PaymentStatusUpdate(java.lang.String)
     */
    @Override
    public boolean unlock4PaymentStatusUpdate(String paymentRequestId) {
        return paymentStatusLockRepo.remove(paymentRequestId) != null;
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#isPaymentStatusSuccess(java.lang.String)
     */
    @Override
    public boolean isPaymentStatusSuccess(String paymentRequestId) {
        return RandomUtils.nextBoolean();
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentContextCallback#isPaymentStatusCancelled(java.lang.String)
     */
    @Override
    public boolean isPaymentStatusCancelled(String paymentRequestId) {
        return RandomUtils.nextBoolean();
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentStatusUpdateCallback#onPaymentSuccess(com.alipay.ams.domain.PaymentResultModel)
     */
    @Override
    protected void onPaymentSuccess(PaymentResultModel paymentResultModel) {
        getSetting().logger.info("onPaymentSuccess [%s]", paymentResultModel);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentStatusUpdateCallback#onPaymentCancelled(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void onPaymentCancelled(String paymentRequestId, String paymentId, String cancelTime) {
        getSetting().logger.info("onPaymentCancelled [%s]", paymentRequestId);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentStatusUpdateCallback#onPaymentFailed(java.lang.String, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onPaymentFailed(String paymentRequestId, ResponseResult responseResult) {
        getSetting().logger.info("onPaymentFailed [%s]", paymentRequestId);
    }

    /** 
     * @see com.alipay.ams.callbacks.PaymentStatusUpdateCallback#reportCancelResultUnknown(com.alipay.ams.AMSClient, com.alipay.ams.domain.requests.PaymentCancelRequest)
     */
    @Override
    public void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request) {
        getSetting().logger.info("reportCancelResultUnknown [%s]", request);
    }

}
