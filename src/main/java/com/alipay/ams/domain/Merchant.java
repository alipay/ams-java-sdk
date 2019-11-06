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

/**
 * 
 * @author guangling.zgl
 * @version $Id: Merchant.java, v 0.1 2019年10月25日 上午12:01:43 guangling.zgl Exp $
 */
public class Merchant {

    private String referenceMerchantId;
    private String merchantName;
    private String merchantMCC;
    private Store  store;

    /**
     * @param referenceMerchantId
     * @param merchantMCC
     * @param string 
     * @param store
     */
    public Merchant(String merchantName, String referenceMerchantId, String merchantMCC, Store store) {
        this.merchantName = merchantName;
        this.referenceMerchantId = referenceMerchantId;
        this.merchantMCC = merchantMCC;
        this.store = store;
    }

    /**
     * Getter method for property <tt>referenceMerchantId</tt>.
     * 
     * @return property value of referenceMerchantId
     */
    public String getReferenceMerchantId() {
        return referenceMerchantId;
    }

    /**
     * Setter method for property <tt>referenceMerchantId</tt>.
     * 
     * @param referenceMerchantId value to be assigned to property referenceMerchantId
     */
    public void setReferenceMerchantId(String referenceMerchantId) {
        this.referenceMerchantId = referenceMerchantId;
    }

    /**
     * Getter method for property <tt>merchantMCC</tt>.
     * 
     * @return property value of merchantMCC
     */
    public String getMerchantMCC() {
        return merchantMCC;
    }

    /**
     * Setter method for property <tt>merchantMCC</tt>.
     * 
     * @param merchantMCC value to be assigned to property merchantMCC
     */
    public void setMerchantMCC(String merchantMCC) {
        this.merchantMCC = merchantMCC;
    }

    /**
     * Getter method for property <tt>store</tt>.
     * 
     * @return property value of store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Setter method for property <tt>store</tt>.
     * 
     * @param store value to be assigned to property store
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Getter method for property <tt>merchantName</tt>.
     * 
     * @return property value of merchantName
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Setter method for property <tt>merchantName</tt>.
     * 
     * @param merchantName value to be assigned to property merchantName
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
