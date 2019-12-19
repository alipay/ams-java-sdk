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

import java.util.HashMap;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: RequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class RequestHeader extends Header {

    private String              requestTime;
    private String              sdkVersion   = AMSSettings.sdkVersion;
    private Map<String, String> extraHeaders = new HashMap<String, String>();

    /** 
     * @param enableTelemetry 
     * @see com.alipay.ams.domain.Header#toMap()
     */
    @Override
    public Map<String, String> toMap() {

        Map<String, String> map = super.toMap();
        map.put("request-time", requestTime);
        map.put("X-sdkVersion", sdkVersion);

        return map;
    }

    /** 
     * @see com.alipay.ams.domain.Header#extraHeaders()
     */
    @Override
    public Map<String, String> extraHeaders() {
        return extraHeaders;
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
     * Getter method for property <tt>sdkVersion</tt>.
     * 
     * @return property value of sdkVersion
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    /**
     * Setter method for property <tt>extraHeaders</tt>.
     * 
     * @param extraHeaders value to be assigned to property extraHeaders
     */
    public void setExtraHeaders(Map<String, String> extraHeaders) {

        if (extraHeaders != null) {
            this.extraHeaders.putAll(extraHeaders);
        }
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void putExtraHeader(String key, String value) {
        this.extraHeaders.put(key, value);
    }
}
