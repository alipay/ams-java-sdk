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

import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Signature.java, v 0.1 2019年10月16日 上午11:48:09 guangling.zgl Exp $
 */
public class Signature {

    String algorithm  = "RSA256";
    String keyVersion = "1";
    String signature;

    public Signature() {

    }

    /**
     * @param headString - something like 'algorithm=RSA256,keyVersion=2,signature=Lv06x'
     */
    public Signature(String headString) {
        this.algorithm = StringUtil.substringBetween(headString, "algorithm=", ",");
        this.keyVersion = StringUtil.substringBetween(headString, "keyVersion=", ",");
        this.signature = StringUtil.substringAfter(headString, "signature=");
    }

    public String toString() {
        return String.format("algorithm=%s,keyVersion=%s,signature=%s", algorithm, keyVersion,
            signature);
    }

    public boolean validate() {
        return StringUtil.isNotBlank(signature);
    }

    /**
     * Getter method for property <tt>algorithm</tt>.
     * 
     * @return property value of algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Setter method for property <tt>algorithm</tt>.
     * 
     * @param algorithm value to be assigned to property algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Getter method for property <tt>keyVersion</tt>.
     * 
     * @return property value of keyVersion
     */
    public String getKeyVersion() {
        return keyVersion;
    }

    /**
     * Setter method for property <tt>keyVersion</tt>.
     * 
     * @param keyVersion value to be assigned to property keyVersion
     */
    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }

    /**
     * Getter method for property <tt>signature</tt>.
     * 
     * @return property value of signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Setter method for property <tt>signature</tt>.
     * 
     * @param signature value to be assigned to property signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
