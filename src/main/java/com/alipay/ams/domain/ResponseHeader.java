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

import java.util.Map;

import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: RequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class ResponseHeader extends Header {

    private String responseTime;
    private String tracerId;

    public ResponseHeader(Map<String, String> headers) {
        super.fromMap(headers);
    }

    /** 
     * @see com.alipay.ams.domain.Header#fromMap(java.util.Map)
     */
    @Override
    protected void fromMapExt(Map<String, String> lowered) {
        this.responseTime = lowered.get("response-time");
        this.tracerId = lowered.get("tracerid");
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
        return StringUtil.isNotBlank(getClientId())
               && StringUtil.isNotBlank(getSignature().getSignature());
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
