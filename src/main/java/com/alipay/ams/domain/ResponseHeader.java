/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: RequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class ResponseHeader extends Header {

    String responseTime;

    public ResponseHeader(Map<String, String> headers) {

        this.setClientId(headers.get("client-id"));
        this.setContentType(headers.get("Content-Type"));
        this.setResponseTime(headers.get("Response-Time"));
        Signature signature = new Signature();
        signature.setSignature(headers.get("Signature"));
        this.setSignature(signature);
    }

    /**
     * Getter method for property <tt>responseTime</tt>.
     * 
     * @return property value of responseTime
     */
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * Setter method for property <tt>responseTime</tt>.
     * 
     * @param responseTime value to be assigned to property responseTime
     */
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * 
     */
    public void validate() {
    }

}
