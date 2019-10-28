/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.callbacks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.ams.domain.PaymentContext;

/**
 * 
 * @author guangling.zgl
 * @version $Id: InMemoryPaymentContextCallback.java, v 0.1 2019年10月25日 上午11:13:54 guangling.zgl Exp $
 */
public class InMemoryPaymentContextCallback implements PaymentContextCallback {

    private final static Map<String, PaymentContext> repository = new ConcurrentHashMap<String, PaymentContext>();

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#loadContextByPaymentRequestIdOrDefault(java.lang.String, com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public PaymentContext loadContextByPaymentRequestIdOrDefault(String paymentRequestId,
                                                                 PaymentContext initial) {

        return repository.containsKey(paymentRequestId) ? repository.get(paymentRequestId)
            : initial;

    }

    /** 
     * @see com.alipay.ams.domain.callbacks.PaymentContextCallback#saveContext(com.alipay.ams.domain.PaymentContext)
     */
    @Override
    public void saveContext(PaymentContext context) {
        repository.put(context.getPaymentRequestId(), context);
    }
}
