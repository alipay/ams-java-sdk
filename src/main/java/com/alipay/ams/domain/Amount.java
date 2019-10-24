/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Currency;
import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Amount.java, v 0.1 2019年10月16日 下午6:54:19 guangling.zgl Exp $
 */
public class Amount {

    private String currency;

    private String value;

    /**
     * @param currency
     * @param value
     */
    public Amount(Currency currency, Long value) {
        this.currency = currency.getCurrencyCode();
        this.value = value.toString();
    }

    public static Amount fromMap(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        return new Amount(Currency.getInstance(map.get("currency")), Long.parseLong(map
            .get("value")));
    }

    /**
     * Getter method for property <tt>currency</tt>.
     * 
     * @return property value of currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter method for property <tt>currency</tt>.
     * 
     * @param currency value to be assigned to property currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Getter method for property <tt>value</tt>.
     * 
     * @return property value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter method for property <tt>value</tt>.
     * 
     * @param value value to be assigned to property value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
