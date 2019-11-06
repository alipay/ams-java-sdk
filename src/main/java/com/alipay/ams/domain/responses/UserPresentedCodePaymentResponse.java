/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
