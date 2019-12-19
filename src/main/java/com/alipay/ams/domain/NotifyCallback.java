/*
 * The MIT License
 * Copyright © 2019 Alipay.com Inc.
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
package com.alipay.ams.domain;

import java.util.HashMap;

import com.alipay.ams.callbacks.PaymentContextSupport;
import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyCallback.java, v 0.1 2019年10月23日 上午11:19:04 guangling.zgl Exp $
 */
public abstract class NotifyCallback {

    private PaymentContextSupport paymentContextSupport;

    /**
     * @param paymentContextSupport
     */
    public NotifyCallback(PaymentContextSupport paymentContextSupport) {
        super();
        this.paymentContextSupport = paymentContextSupport;
    }

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

        PaymentResultModel paymentResultModel = new PaymentResultModel(body,
            notifyRequestHeader.getAgentToken());

        PaymentContext paymentContext = paymentContextSupport
            .loadContextByPaymentRequestIdOrDefault(paymentResultModel.getPaymentRequestId(), null);

        if (paymentContext != null) {

            paymentContext.getTelemetry().setNotifiedPaymentSuccess();
            paymentContextSupport.saveContext(paymentContext);

        } else {

            settings.logger.warn(
                "PaymentContext not found and ignored in NotifyCallback. paymentRequestId=[%s]",
                paymentResultModel.getPaymentRequestId());

        }

        onPaymentSuccess(settings, notifyRequestHeader, paymentResultModel);
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

    /**
     * 
     * @param settings
     * @param notifyRequestHeader
     * @param authNotifyModel
     */
    protected abstract void onAuthNotify(AMSSettings settings,
                                         NotifyRequestHeader notifyRequestHeader,
                                         AuthNotifyModel authNotifyModel);

    /**
     * 
     * @param settings
     * @param notifyRequestHeader
     * @param body
     */
    public void onNonPaymentNotify(AMSSettings settings, NotifyRequestHeader notifyRequestHeader,
                                   HashMap<String, Object> body) {
        if (body.containsKey("accessToken")) {
            //an authorization notify
            AuthNotifyModel authNotifyModel = new AuthNotifyModel(body);
            onAuthNotify(settings, notifyRequestHeader, authNotifyModel);

        } else {
            //currently unknown notify 
            settings.logger.warn("Received an unknown notify: [%s]", body);
        }
    }

}
