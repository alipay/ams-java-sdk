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

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryResponse.java, v 0.1 2019年10月18日 下午6:12:10 guangling.zgl Exp $
 */
public class AuthNotifyModel {

    private String clientId;
    private String authClientId;
    private String authTime;
    private String accessToken;
    private String accessTokenExpiryTime;
    private String customerId;

    /** 
     * @see com.alipay.ams.domain.Response#initBody(java.util.HashMap)
     */
    public AuthNotifyModel(HashMap<String, Object> body) {

        this.clientId = (String) body.get("clientId");
        this.authClientId = (String) body.get("authClientId");
        this.authTime = (String) body.get("authTime");
        this.accessToken = (String) body.get("accessToken");
        this.accessTokenExpiryTime = (String) body.get("accessTokenExpiryTime");
        this.customerId = (String) body.get("customerId");

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
     * Getter method for property <tt>authClientId</tt>.
     * 
     * @return property value of authClientId
     */
    public String getAuthClientId() {
        return authClientId;
    }

    /**
     * Setter method for property <tt>authClientId</tt>.
     * 
     * @param authClientId value to be assigned to property authClientId
     */
    public void setAuthClientId(String authClientId) {
        this.authClientId = authClientId;
    }

    /**
     * Getter method for property <tt>authTime</tt>.
     * 
     * @return property value of authTime
     */
    public String getAuthTime() {
        return authTime;
    }

    /**
     * Setter method for property <tt>authTime</tt>.
     * 
     * @param authTime value to be assigned to property authTime
     */
    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    /**
     * Getter method for property <tt>accessToken</tt>.
     * 
     * @return property value of accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Setter method for property <tt>accessToken</tt>.
     * 
     * @param accessToken value to be assigned to property accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Getter method for property <tt>accessTokenExpiryTime</tt>.
     * 
     * @return property value of accessTokenExpiryTime
     */
    public String getAccessTokenExpiryTime() {
        return accessTokenExpiryTime;
    }

    /**
     * Setter method for property <tt>accessTokenExpiryTime</tt>.
     * 
     * @param accessTokenExpiryTime value to be assigned to property accessTokenExpiryTime
     */
    public void setAccessTokenExpiryTime(String accessTokenExpiryTime) {
        this.accessTokenExpiryTime = accessTokenExpiryTime;
    }

    /**
     * Getter method for property <tt>customerId</tt>.
     * 
     * @return property value of customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Setter method for property <tt>customerId</tt>.
     * 
     * @param customerId value to be assigned to property customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

}
