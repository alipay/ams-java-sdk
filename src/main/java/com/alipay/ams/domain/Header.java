/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Header.java, v 0.1 2019年10月16日 上午11:46:27 guangling.zgl Exp $
 */
public abstract class Header {

    String    contentType = "application/json; charset=UTF-8";
    String    clientId;
    Signature signature;

    public Map<String, String> toMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Content-Type", contentType);
        hashMap.put("client-id", clientId);
        hashMap.put("Signature", signature.toString());

        Map<String, String> extraHeaders = extraHeaders();
        if (extraHeaders != null) {
            for (Entry<String, String> e : extraHeaders.entrySet()) {
                hashMap.put(e.getKey(), e.getValue());
            }
        }
        return hashMap;
    }

    public Map<String, String> extraHeaders() {
        return null;
    }

    /**
     * Getter method for property <tt>contentType</tt>.
     * 
     * @return property value of contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Setter method for property <tt>contentType</tt>.
     * 
     * @param contentType value to be assigned to property contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Getter method for property <tt>clientId</tt>.
     * 
     * @return property value of clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Setter method for property <tt>clientId</tt>.
     * 
     * @param clientId value to be assigned to property clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Getter method for property <tt>signature</tt>.
     * 
     * @return property value of signature
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Setter method for property <tt>signature</tt>.
     * 
     * @param signature value to be assigned to property signature
     */
    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
