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
