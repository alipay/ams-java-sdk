/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Map;

import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: RequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class ResponseHeader extends Header {

    String responseTime;
    String tracerId;

    public ResponseHeader(Map<String, String> headers) {

        this.setClientId(headers.get("client-id"));
        this.setTracerId(headers.get("tracerId"));
        this.setContentType(headers.get("Content-Type"));
        this.setResponseTime(headers.get("response-time"));
        this.setSignature(new Signature(headers.get("signature")));
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
    public boolean validate() {
        return StringUtil.isNotBlank(this.clientId)
               && StringUtil.isNotBlank(this.signature.getSignature());
    }

    /**
     * Getter method for property <tt>tracerId</tt>.
     * 
     * @return property value of tracerId
     */
    public String getTracerId() {
        return tracerId;
    }

    /**
     * Setter method for property <tt>tracerId</tt>.
     * 
     * @param tracerId value to be assigned to property tracerId
     */
    public void setTracerId(String tracerId) {
        this.tracerId = tracerId;
    }

}
