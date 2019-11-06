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
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Header.java, v 0.1 2019年10月16日 上午11:46:27 guangling.zgl Exp $
 */
public abstract class Header {

    private String    contentType = "application/json; charset=UTF-8";
    private String    clientId;
    private String    agentToken;
    private Signature signature;

    /**
     * 
     */
    protected void fromMap(Map<String, String> headers) {

        HashMap<String, String> lowered = new HashMap<String, String>();

        for (Entry<String, String> e : headers.entrySet()) {
            lowered.put(e.getKey().toLowerCase(), e.getValue());
        }

        this.clientId = lowered.get("client-id");
        this.contentType = lowered.get("content-type");
        this.signature = new Signature(lowered.get("signature"));
        this.agentToken = lowered.get("agent-token");

        fromMapExt(lowered);
    }

    /**
     * 
     * @param lowered
     */
    protected void fromMapExt(Map<String, String> lowered) {
    }

    /**
     * 
     * @return
     */
    public Map<String, String> toMap() {

        HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("Content-Type", contentType);
        hashMap.put("client-id", clientId);
        hashMap.put("signature", signature.toString());

        if (StringUtil.isNotBlank(agentToken)) {
            hashMap.put("Agent-Token", agentToken);
        }

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
     * Getter method for property <tt>agentToken</tt>.
     * 
     * @return property value of agentToken
     */
    public String getAgentToken() {
        return agentToken;
    }

    /**
     * Setter method for property <tt>agentToken</tt>.
     * 
     * @param agentToken value to be assigned to property agentToken
     */
    public void setAgentToken(String agentToken) {
        this.agentToken = agentToken;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
