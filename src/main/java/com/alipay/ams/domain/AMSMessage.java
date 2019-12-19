/*
 * The MIT License
 * Copyright © 2019 Alipay.com Inc.
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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMSMessage.java, v 0.1 2019年10月16日 上午11:49:48 guangling.zgl Exp $
 */
public abstract class AMSMessage {

    private AMSSettings settings;
    private String      requestURI;
    private String      agentToken;

    protected AMSMessage(String requestURI, AMSSettings settings) {
        this.requestURI = requestURI;
        this.settings = settings;
    }

    /**
     * For log message
     * 
     * @return
     */
    public abstract String getBizIdentifier();

    /**
     * 
     * @return
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Getter method for property <tt>settings</tt>.
     * 
     * @return property value of settings
     */
    public AMSSettings getSettings() {
        return settings;
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
