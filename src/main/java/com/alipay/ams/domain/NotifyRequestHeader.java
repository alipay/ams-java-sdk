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

    private String requestTime;
    private String tracerId;

    /**
     * @param notifyRequestheaders
     */
    public NotifyRequestHeader(Map<String, String> headers) {
        super.fromMap(headers);
    }

    /** 
     * @see com.alipay.ams.domain.Header#fromMapExt(java.util.Map)
     */
    @Override
    protected void fromMapExt(Map<String, String> lowered) {
        this.requestTime = lowered.get("request-time");
        this.tracerId = lowered.get("tracerid");
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
