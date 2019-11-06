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
package com.alipay.ams.domain.responses;

import java.util.HashMap;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.Response;
import com.alipay.ams.domain.ResponseHeader;

/**
 * A SUCCESS payment response.
 * 
 * @author guangling.zgl
 * @version $Id: UserPresentedCodePaymentResponse.java, v 0.1 2019年10月16日 下午8:42:50 guangling.zgl Exp $
 */
public class UserPresentedCodePaymentResponse extends Response {

    private PaymentResultModel paymentResultModel;

    /**
     * @param settings
     * @param requestURI
     * @param responseHeader
     * @param body
     */
    public UserPresentedCodePaymentResponse(AMSSettings settings, String requestURI,
                                            ResponseHeader responseHeader,
                                            HashMap<String, Object> body) {
        super(requestURI, settings, responseHeader, body);
    }

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap, com.alipay.ams.domain.ResponseHeader)
     */
    @Override
    protected void initBody(HashMap<String, Object> body, ResponseHeader responseHeader) {

        //To reuse PaymentResultModel, we add the missing paymentStatus field.
        if (!body.containsKey("paymentStatus")) {
            body.put("paymentStatus", "SUCCESS");
        }

        this.paymentResultModel = new PaymentResultModel(body, responseHeader.getAgentToken());

    }

    /** 
     * @see com.alipay.ams.domain.AMSMessage#getBizIdentifier()
     */
    @Override
    public String getBizIdentifier() {
        return paymentResultModel.getPaymentRequestId();
    }

    /**
     * Getter method for property <tt>paymentResultModel</tt>.
     * 
     * @return property value of paymentResultModel
     */
    public PaymentResultModel getPaymentResultModel() {
        return paymentResultModel;
    }

}
