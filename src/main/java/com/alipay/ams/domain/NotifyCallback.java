/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.reponses.PaymentResultModel;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyCallback.java, v 0.1 2019年10月23日 上午11:19:04 guangling.zgl Exp $
 */
public abstract class NotifyCallback {

    /**
     * 
     * @param notifyRequestHeader
     * @param requestBody
     */
    public void onNotifySignatureVerifyFailed(NotifyRequestHeader notifyRequestHeader,
                                              String requestBody) {
    }

    /**
     * 
     * @param notifyResult
     */
    public void onFstatus(ResponseResult notifyResult) {
    }

    /**
     * 
     * @param notifyResult
     */
    public void onUstatus(ResponseResult notifyResult) {
    }

    /**
     * 
     * @param settings
     * @param notifyRequestHeader
     * @param body
     */
    public void onSstatus(AMSSettings settings, NotifyRequestHeader notifyRequestHeader,
                          HashMap<String, Object> body) {
        onPaymentSuccess(settings, notifyRequestHeader, new PaymentResultModel(body));
    }

    /**
     * 
     * @param settings
     * @param notifyRequestHeader
     * @param paymentResultModel
     */
    protected abstract void onPaymentSuccess(AMSSettings settings,
                                             NotifyRequestHeader notifyRequestHeader,
                                             PaymentResultModel paymentResultModel);

}
