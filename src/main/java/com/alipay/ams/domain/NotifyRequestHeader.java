/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
        HashMap<String, String> lowered = new HashMap<String, String>();

        for (Entry<String, String> e : headers.entrySet()) {
            lowered.put(e.getKey().toLowerCase(), e.getValue());
        }

        this.setClientId(lowered.get("client-id"));
        this.setContentType(lowered.get("content-type"));
        this.setRequestTime(lowered.get("request-time"));
        this.setSignature(new Signature(lowered.get("signature")));
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
