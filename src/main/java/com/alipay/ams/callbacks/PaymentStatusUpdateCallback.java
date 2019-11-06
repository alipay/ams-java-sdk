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
package com.alipay.ams.callbacks;

import java.util.concurrent.TimeUnit;

import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.job.JobExecutor;
import com.alipay.ams.util.LockUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentStatusUpdateCallback.java, v 0.1 2019年10月29日 下午7:13:33 guangling.zgl Exp $
 */
public abstract class PaymentStatusUpdateCallback {

    private AMSSettings setting;

    private LockSupport lockSupport;

    /**
     * @param setting
     * @param lockSupport
     */
    public PaymentStatusUpdateCallback(AMSSettings setting, LockSupport lockSupport) {
        super();
        this.setting = setting;
        this.lockSupport = lockSupport;
    }

    public void handlePaymentSuccess(final PaymentResultModel paymentResultModel) {

        boolean lockOK = LockUtil.executeWithLock(lockSupport,
            paymentResultModel.getPaymentRequestId(), new Runnable() {

                @Override
                public void run() {

                    if (isPaymentStatusSuccess(paymentResultModel.getPaymentRequestId())) {

                        setting.logger
                            .warn(
                                "onPaymentSuccess() skipped. Because Payment status now is already SUCCESS in partner system. paymentResultModel=[%s]",
                                paymentResultModel);
                        return;
                    }

                    if (isPaymentStatusCancelled(paymentResultModel.getPaymentRequestId())) {

                        setting.logger
                            .warn(
                                "onPaymentSuccess() skipped. Because Payment status now is already Cancelled in partner system. paymentResultModel=[%s]",
                                paymentResultModel);
                        return;
                    }

                    onPaymentSuccess(paymentResultModel);

                }
            });

        if (!lockOK) { //Maybe there is a pending Cancel operation.

            //In memory retry.
            setting.logger
                .warn(
                    "onPaymentSuccess() delayed in %s seconds. Because Acquiring lock failed. paymentResultModel=[%s]",
                    setting.retryHandlePaymentSuccessDelayInSeconds, paymentResultModel);

            final PaymentStatusUpdateCallback self = this;

            JobExecutor.instance.schedule(new Runnable() {

                @Override
                public void run() {
                    setting.logger
                        .warn(
                            "onPaymentSuccess() running from a scheduled task. paymentResultModel=[%s]",
                            paymentResultModel);
                    self.handlePaymentSuccess(paymentResultModel);
                }
            }, setting.retryHandlePaymentSuccessDelayInSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * Not intended to be used directly. Use handlePaymentSuccess due to the lock logic.
     * 
     * @param paymentResultModel
     */
    protected abstract void onPaymentSuccess(PaymentResultModel paymentResultModel);

    public abstract void onPaymentCancelled(String paymentRequestId, String paymentId,
                                            String cancelTime);

    public abstract void onPaymentFailed(String paymentRequestId, ResponseResult responseResult);

    /**
     * 
     * @param client
     * @param request
     */
    public abstract void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request);

    /**
     * 
     * @param paymentRequestId
     * @return
     */
    abstract public boolean isPaymentStatusSuccess(String paymentRequestId);

    /**
     * 
     * @param paymentRequestId
     * @return
     */
    abstract public boolean isPaymentStatusCancelled(String paymentRequestId);

    /**
     * Getter method for property <tt>setting</tt>.
     * 
     * @return property value of setting
     */
    public AMSSettings getSetting() {
        return setting;
    }

    /**
     * Getter method for property <tt>lockSupport</tt>.
     * 
     * @return property value of lockSupport
     */
    public LockSupport getLockSupport() {
        return lockSupport;
    }
}
