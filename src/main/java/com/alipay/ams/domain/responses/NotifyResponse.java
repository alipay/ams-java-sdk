/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.responses;

import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyResponse.java, v 0.1 2019年10月23日 上午11:05:26 guangling.zgl Exp $
 */
public class NotifyResponse {
    private byte[]              bodyContent;
    private Map<String, String> responseHeaders;

    /**
     * Getter method for property <tt>bodyContent</tt>.
     * 
     * @return property value of bodyContent
     */
    public byte[] getBodyContent() {
        return bodyContent;
    }

    /**
     * Setter method for property <tt>bodyContent</tt>.
     * 
     * @param bodyContent value to be assigned to property bodyContent
     */
    public void setBodyContent(byte[] bodyContent) {
        this.bodyContent = bodyContent;
    }

    /**
     * Getter method for property <tt>responseHeaders</tt>.
     * 
     * @return property value of responseHeaders
     */
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Setter method for property <tt>responseHeaders</tt>.
     * 
     * @param responseHeaders value to be assigned to property responseHeaders
     */
    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

}
