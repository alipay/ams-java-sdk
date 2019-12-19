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

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alipay.ams.util.DateUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Quote.java, v 0.1 2019年10月28日 上午10:36:46 guangling.zgl Exp $
 */
public class Quote {

    private String  quoteId;
    private String  quoteCurrencyPair;
    private Double  quotePrice;
    private Date    quoteStartTime;
    private Date    quoteExpiryTime;
    private boolean guaranteed;

    /**
     * 
     * @param map
     * @return
     */
    public static Quote fromMap(Map<String, Object> map) {

        if (map == null || map.isEmpty()) {
            return null;
        }

        Quote quote = new Quote();
        quote.setQuoteId((String) map.get("quoteId"));
        quote.setQuoteCurrencyPair((String) map.get("quoteCurrencyPair"));
        quote.setQuotePrice((Double) map.get("quotePrice"));
        quote.setQuoteStartTime(DateUtil.parseISODateTimeStr((String) map.get("quoteStartTime")));
        quote.setQuoteExpiryTime(DateUtil.parseISODateTimeStr((String) map.get("quoteExpiryTime")));
        quote.setGuaranteed(Boolean.valueOf((Boolean) map.get("guaranteed")));

        return quote;
    }

    /**
     * Getter method for property <tt>quoteId</tt>.
     * 
     * @return property value of quoteId
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * Setter method for property <tt>quoteId</tt>.
     * 
     * @param quoteId value to be assigned to property quoteId
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /**
     * Getter method for property <tt>quoteCurrencyPair</tt>.
     * 
     * @return property value of quoteCurrencyPair
     */
    public String getQuoteCurrencyPair() {
        return quoteCurrencyPair;
    }

    /**
     * Setter method for property <tt>quoteCurrencyPair</tt>.
     * 
     * @param quoteCurrencyPair value to be assigned to property quoteCurrencyPair
     */
    public void setQuoteCurrencyPair(String quoteCurrencyPair) {
        this.quoteCurrencyPair = quoteCurrencyPair;
    }

    /**
     * Getter method for property <tt>quotePrice</tt>.
     * 
     * @return property value of quotePrice
     */
    public Double getQuotePrice() {
        return quotePrice;
    }

    /**
     * Setter method for property <tt>quotePrice</tt>.
     * 
     * @param quotePrice value to be assigned to property quotePrice
     */
    public void setQuotePrice(Double quotePrice) {
        this.quotePrice = quotePrice;
    }

    /**
     * Getter method for property <tt>quoteStartTime</tt>.
     * 
     * @return property value of quoteStartTime
     */
    public Date getQuoteStartTime() {
        return quoteStartTime;
    }

    /**
     * Setter method for property <tt>quoteStartTime</tt>.
     * 
     * @param quoteStartTime value to be assigned to property quoteStartTime
     */
    public void setQuoteStartTime(Date quoteStartTime) {
        this.quoteStartTime = quoteStartTime;
    }

    /**
     * Getter method for property <tt>quoteExpiryTime</tt>.
     * 
     * @return property value of quoteExpiryTime
     */
    public Date getQuoteExpiryTime() {
        return quoteExpiryTime;
    }

    /**
     * Setter method for property <tt>quoteExpiryTime</tt>.
     * 
     * @param quoteExpiryTime value to be assigned to property quoteExpiryTime
     */
    public void setQuoteExpiryTime(Date quoteExpiryTime) {
        this.quoteExpiryTime = quoteExpiryTime;
    }

    /**
     * Getter method for property <tt>guaranteed</tt>.
     * 
     * @return property value of guaranteed
     */
    public boolean isGuaranteed() {
        return guaranteed;
    }

    /**
     * Setter method for property <tt>guaranteed</tt>.
     * 
     * @param guaranteed value to be assigned to property guaranteed
     */
    public void setGuaranteed(boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
