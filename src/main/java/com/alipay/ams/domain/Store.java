/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
