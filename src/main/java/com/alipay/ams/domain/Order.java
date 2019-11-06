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
 * @version $Id: Order.java, v 0.1 2019年10月16日 下午7:37:20 guangling.zgl Exp $
 */
public class Order {
    private String   referenceOrderId;
    private String   orderDescription;
    private Amount   orderAmount;
    private Merchant merchant;

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

    /**
     * Getter method for property <tt>merchant</tt>.
     * 
     * @return property value of merchant
     */
    public Merchant getMerchant() {
        return merchant;
    }

    /**
     * Setter method for property <tt>merchant</tt>.
     * 
     * @param merchant value to be assigned to property merchant
     */
    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

}
