/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

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

    private AMSSettings            setting;

    private PaymentContextCallback paymentContextCallback;

    void handlePaymentSuccess(final PaymentResultModel paymentResultModel) {

        boolean lockOK = LockUtil.executeWithLock(paymentContextCallback,
            paymentResultModel.getPaymentRequestId(), new Runnable() {

                @Override
                public void run() {

                    if (paymentContextCallback.isPaymentStatusSuccess(paymentResultModel
                        .getPaymentRequestId())) {

                        setting.logger
                            .warn(
                                "onPaymentSuccess() skipped. Because Payment status now is already SUCCESS in partner system. paymentResultModel=[%s]",
                                paymentResultModel);
                        return;
                    }

                    if (paymentContextCallback.isPaymentStatusCancelled(paymentResultModel
                        .getPaymentRequestId())) {

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

    abstract void onPaymentSuccess(PaymentResultModel paymentResultModel);

    abstract void onPaymentCancelled(String paymentRequestId, String paymentId, String cancelTime);

    abstract void onPaymentFailed(String paymentRequestId, ResponseResult responseResult);

    /**
     * 
     * @param client
     * @param request
     */
    abstract void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request);
}
