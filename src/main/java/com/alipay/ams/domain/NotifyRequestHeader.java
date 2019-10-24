/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyRequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class NotifyRequestHeader extends Header {

    String requestTime;

    /**
     * @param notifyRequestheaders
     */
    public NotifyRequestHeader(Map<String, String> headers) {
        this.setClientId(headers.get("client-id"));
        this.setContentType(headers.get("Content-Type"));
        this.setRequestTime(headers.get("Request-Time"));
        Signature signature = new Signature();
        signature.setSignature(headers.get("Signature"));
        this.setSignature(signature);

    }

    /**
     * 
     */
    public void validate() {
    }

    /**
     * Getter method for property <tt>requestTime</tt>.
     * 
     * @return property value of requestTime
     */
    public String getRequestTime() {
        return requestTime;
    }

    /**
     * Setter method for property <tt>requestTime</tt>.
     * 
     * @param requestTime value to be assigned to property requestTime
     */
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

}
