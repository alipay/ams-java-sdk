/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.ams.domain;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Env.java, v 0.1 2020年2月20日 下午8:05:49 guangling.zgl Exp $
 */
public class Env {

    /**  */
    private String userAgent;

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

}
