/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.util;

import com.alipay.ams.domain.callbacks.PaymentContextCallback;

/**
 * 
 * @author guangling.zgl
 * @version $Id: LockUtil.java, v 0.1 2019年10月29日 下午6:03:06 guangling.zgl Exp $
 */
public class LockUtil {

    public static boolean executeWithLock(PaymentContextCallback paymentContextCallback,
                                          String paymentRequestId, Runnable logic) {

        if (paymentContextCallback.tryLock4PaymentStatusUpdate(paymentRequestId)) {

            try {

                logic.run();
                return true;

            } finally {
                paymentContextCallback.unlock4PaymentStatusUpdate(paymentRequestId);
            }

        } else {
            return false;
        }
    }
}
