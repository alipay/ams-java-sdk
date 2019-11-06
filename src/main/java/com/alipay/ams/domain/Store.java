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
 * @version $Id: Store.java, v 0.1 2019年10月25日 上午12:03:44 guangling.zgl Exp $
 */
public class Store {
    private String referenceStoreId;
    private String storeName;
    private String storeMCC;

    /**
     * @param storeName
     * @param referenceStoreId
     * @param storeMCC
     */
    public Store(String storeName, String referenceStoreId, String storeMCC) {
        this.storeName = storeName;
        this.referenceStoreId = referenceStoreId;
        this.storeMCC = storeMCC;
    }

    /**
     * Getter method for property <tt>referenceStoreId</tt>.
     * 
     * @return property value of referenceStoreId
     */
    public String getReferenceStoreId() {
        return referenceStoreId;
    }

    /**
     * Setter method for property <tt>referenceStoreId</tt>.
     * 
     * @param referenceStoreId value to be assigned to property referenceStoreId
     */
    public void setReferenceStoreId(String referenceStoreId) {
        this.referenceStoreId = referenceStoreId;
    }

    /**
     * Getter method for property <tt>storeName</tt>.
     * 
     * @return property value of storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Setter method for property <tt>storeName</tt>.
     * 
     * @param storeName value to be assigned to property storeName
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * Getter method for property <tt>storeMCC</tt>.
     * 
     * @return property value of storeMCC
     */
    public String getStoreMCC() {
        return storeMCC;
    }

    /**
     * Setter method for property <tt>storeMCC</tt>.
     * 
     * @param storeMCC value to be assigned to property storeMCC
     */
    public void setStoreMCC(String storeMCC) {
        this.storeMCC = storeMCC;
    }

}
