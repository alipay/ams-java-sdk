/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import com.alipay.ams.domain.PaymentContext;

/**
 * 
 * store & retrieve context info for a payment.
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContextCallback.java, v 0.1 2019年10月23日 下午3:51:48 guangling.zgl Exp $
 */
public interface PaymentContextCallback {

    /**
     * 
     * @param paymentRequestId
     * @param initial
     * @return
     */
    public PaymentContext loadContextByPaymentRequestIdOrDefault(String paymentRequestId,
                                                                 PaymentContext initial);

    public void saveContext(PaymentContext context);

}
