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

import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: RequestHeader.java, v 0.1 2019年10月16日 下午3:47:58 guangling.zgl Exp $
 */
public class NotifyResponseHeader extends Header {

    private String              responseTime;

    private String              sdkVersion = AMSSettings.sdkVersion; ;

    private Map<String, String> extraHeaders;

    /** 
     * @see com.alipay.ams.domain.Header#toMap()
     */
    @Override
    public Map<String, String> toMap() {

        Map<String, String> map = super.toMap();
        map.put("response-time", responseTime);
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
        this.extraHeaders = extraHeaders;
    }
}
