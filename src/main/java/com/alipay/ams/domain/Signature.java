/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
    String keyVersion = "2";
    String signature;

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
