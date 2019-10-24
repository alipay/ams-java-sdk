/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
