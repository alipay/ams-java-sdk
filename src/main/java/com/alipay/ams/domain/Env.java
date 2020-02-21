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
