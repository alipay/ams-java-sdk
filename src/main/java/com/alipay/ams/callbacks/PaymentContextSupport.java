/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.callbacks;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.alipay.ams.domain.PaymentContext;

/**
 * 
 * store & retrieve context info for a payment.
 * 
 * A solution with data persistent feature like Redis would be strongly recommended.
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContextSupport.java, v 0.1 2019年10月23日 下午3:51:48 guangling.zgl Exp $
 */
public interface PaymentContextSupport {
    static final int                     MAX_REQUEST_TELEMETRY_BUFFER_SIZE = 100;
    static ConcurrentLinkedQueue<String> prevRequestTelemetry              = new ConcurrentLinkedQueue<String>();

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
