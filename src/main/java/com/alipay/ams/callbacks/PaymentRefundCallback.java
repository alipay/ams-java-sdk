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

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.domain.CallbackWithDumbTelemetry;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentRefundRequest;
import com.alipay.ams.domain.responses.PaymentRefundResponse;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentRefundCallback.java, v 0.1 2019年10月18日 下午6:37:18 guangling.zgl Exp $
 */
public abstract class PaymentRefundCallback
                                           extends
                                           CallbackWithDumbTelemetry<PaymentRefundRequest, PaymentRefundResponse> {

    /**
     * 
     * @param cancelResponse
     */
    protected abstract void onRefundSuccess(PaymentRefundResponse cancelResponse);

    protected abstract void onRefundFailure(String refundRequestId, ResponseResult responseResult);

    /** 
     * @see com.alipay.ams.domain.Callback#onIOException(java.io.IOException, com.alipay.ams.AMSClient, com.alipay.ams.domain.Request)
     */
    @Override
    public abstract void onIOException(IOException e, AMSClient client, PaymentRefundRequest request);

    /** 
     * @see com.alipay.ams.domain.Callback#onHttpStatusNot200(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, int)
     */
    @Override
    public abstract void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code);

    /** 
     * @see com.alipay.ams.domain.Callback#onFstatus(com.alipay.ams.AMSClient, com.alipay.ams.domain.Request, com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public void onFstatus(AMSClient client, PaymentRefundRequest request,
                          ResponseResult responseResult) {
        onRefundFailure(request.getRefundRequestId(), responseResult);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onSstatus(com.alipay.ams.AMSClient, java.lang.String, com.alipay.ams.domain.ResponseHeader, java.util.HashMap, com.alipay.ams.domain.Request)
     */
    @Override
    public void onSstatus(AMSClient client, String requestURI, ResponseHeader responseHeader,
                          HashMap<String, Object> body, PaymentRefundRequest request) {
        PaymentRefundResponse refundResponse = new PaymentRefundResponse(requestURI,
            client.getSettings(), responseHeader, body);
        onRefundSuccess(refundResponse);
    }

    /** 
     * @see com.alipay.ams.domain.Callback#onUstatus(com.alipay.ams.domain.ResponseResult)
     */
    @Override
    public abstract void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                   ResponseResult responseResult);
}
