/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.responses.PaymentCancelResponse;
import com.alipay.ams.util.LockUtil;
import com.alipay.ams.util.Logger;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentStatusUpdateCallback.java, v 0.1 2019年10月29日 下午7:13:33 guangling.zgl Exp $
 */
public abstract class PaymentStatusUpdateCallback {

    Logger                         logger;

    private PaymentContextCallback paymentContextCallback;

    void handlePaymentSuccess(final PaymentResultModel paymentResultModel) {

        boolean lockOK = LockUtil.executeWithLock(paymentContextCallback,
            paymentResultModel.getPaymentRequestId(), new Runnable() {

                @Override
                public void run() {

                    if (paymentContextCallback.isPaymentStatusSuccess(paymentResultModel
                        .getPaymentRequestId())) {

                        logger
                            .warn(
                                "onPaymentSuccess() skipped. Because Payment status now is already SUCCESS in partner system. paymentResultModel=[%s]",
                                paymentResultModel);
                        return;
                    }

                    if (paymentContextCallback.isPaymentStatusCancelled(paymentResultModel
                        .getPaymentRequestId())) {

                        logger
                            .warn(
                                "onPaymentSuccess() skipped. Because Payment status now is already Cancelled in partner system. paymentResultModel=[%s]",
                                paymentResultModel);
                        return;
                    }

                    onPaymentSuccess(paymentResultModel);

                }
            });

        if (!lockOK) {

            logger
                .warn(
                    "onPaymentSuccess() skipped. Because Acquiring lock failed. paymentResultModel=[%s]",
                    paymentResultModel);
        }
    }

    void handlePaymentCancelled(PaymentCancelResponse paymentCancelResponse) {
    }

    void handlePaymentFailed(ResponseResult responseResult) {
    }

    abstract void onPaymentSuccess(PaymentResultModel paymentResultModel);

    abstract void onPaymentCancelled(PaymentCancelResponse paymentCancelResponse);

    abstract void onPaymentFailed(ResponseResult responseResult);
}
