/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Order.java, v 0.1 2019年10月16日 下午7:37:20 guangling.zgl Exp $
 */
public class Order {
    private String referenceOrderId;
    private String orderDescription;
    private Amount orderAmount;

    /**
     * Getter method for property <tt>referenceOrderId</tt>.
     * 
     * @return property value of referenceOrderId
     */
    public String getReferenceOrderId() {
        return referenceOrderId;
    }

    /**
     * Setter method for property <tt>referenceOrderId</tt>.
     * 
     * @param referenceOrderId value to be assigned to property referenceOrderId
     */
    public void setReferenceOrderId(String referenceOrderId) {
        this.referenceOrderId = referenceOrderId;
    }

    /**
     * Getter method for property <tt>orderDescription</tt>.
     * 
     * @return property value of orderDescription
     */
    public String getOrderDescription() {
        return orderDescription;
    }

    /**
     * Setter method for property <tt>orderDescription</tt>.
     * 
     * @param orderDescription value to be assigned to property orderDescription
     */
    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    /**
     * Getter method for property <tt>orderAmount</tt>.
     * 
     * @return property value of orderAmount
     */
    public Amount getOrderAmount() {
        return orderAmount;
    }

    /**
     * Setter method for property <tt>orderAmount</tt>.
     * 
     * @param orderAmount value to be assigned to property orderAmount
     */
    public void setOrderAmount(Amount orderAmount) {
        this.orderAmount = orderAmount;
    }

}
