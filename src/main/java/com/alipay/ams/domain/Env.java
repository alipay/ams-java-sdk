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

/**
 * 
 * @author guangling.zgl
 * @version $Id: Env.java, v 0.1 2020年2月20日 下午8:05:49 guangling.zgl Exp $
 */
public class Env {

    private String terminalType;
    private String osType;
    private String userAgent;
    private String deviceTokenId;
    private String clientIp;
    private String cookieId;
    private String storeTerminalId;
    private String storeTerminalRequestTime;
    private String extendInfo;

    /**
     */
    public Env() {

    }

    /**
     * @param userAgent
     */
    public Env(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Getter method for property <tt>userAgent</tt>.
     * 
     * @return property value of userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Setter method for property <tt>userAgent</tt>.
     * 
     * @param userAgent value to be assigned to property userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Getter method for property <tt>terminalType</tt>.
     * 
     * @return property value of terminalType
     */
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * Setter method for property <tt>terminalType</tt>.
     * 
     * @param terminalType value to be assigned to property terminalType
     */
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    /**
     * Getter method for property <tt>osType</tt>.
     * 
     * @return property value of osType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * Setter method for property <tt>osType</tt>.
     * 
     * @param osType value to be assigned to property osType
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * Getter method for property <tt>deviceTokenId</tt>.
     * 
     * @return property value of deviceTokenId
     */
    public String getDeviceTokenId() {
        return deviceTokenId;
    }

    /**
     * Setter method for property <tt>deviceTokenId</tt>.
     * 
     * @param deviceTokenId value to be assigned to property deviceTokenId
     */
    public void setDeviceTokenId(String deviceTokenId) {
        this.deviceTokenId = deviceTokenId;
    }

    /**
     * Getter method for property <tt>clientIp</tt>.
     * 
     * @return property value of clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Setter method for property <tt>clientIp</tt>.
     * 
     * @param clientIp value to be assigned to property clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * Getter method for property <tt>cookieId</tt>.
     * 
     * @return property value of cookieId
     */
    public String getCookieId() {
        return cookieId;
    }

    /**
     * Setter method for property <tt>cookieId</tt>.
     * 
     * @param cookieId value to be assigned to property cookieId
     */
    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    /**
     * Getter method for property <tt>storeTerminalId</tt>.
     * 
     * @return property value of storeTerminalId
     */
    public String getStoreTerminalId() {
        return storeTerminalId;
    }

    /**
     * Setter method for property <tt>storeTerminalId</tt>.
     * 
     * @param storeTerminalId value to be assigned to property storeTerminalId
     */
    public void setStoreTerminalId(String storeTerminalId) {
        this.storeTerminalId = storeTerminalId;
    }

    /**
     * Getter method for property <tt>storeTerminalRequestTime</tt>.
     * 
     * @return property value of storeTerminalRequestTime
     */
    public String getStoreTerminalRequestTime() {
        return storeTerminalRequestTime;
    }

    /**
     * Setter method for property <tt>storeTerminalRequestTime</tt>.
     * 
     * @param storeTerminalRequestTime value to be assigned to property storeTerminalRequestTime
     */
    public void setStoreTerminalRequestTime(String storeTerminalRequestTime) {
        this.storeTerminalRequestTime = storeTerminalRequestTime;
    }

    /**
     * Getter method for property <tt>extendInfo</tt>.
     * 
     * @return property value of extendInfo
     */
    public String getExtendInfo() {
        return extendInfo;
    }

    /**
     * Setter method for property <tt>extendInfo</tt>.
     * 
     * @param extendInfo value to be assigned to property extendInfo
     */
    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

}
