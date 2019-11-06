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
package com.alipay.ams.job;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Job.java, v 0.1 2019年10月28日 下午6:17:05 guangling.zgl Exp $
 */
public class Job implements Serializable {

    /**  */
    private static final long serialVersionUID = 1430509534871165784L;

    /**
         * 
         * @author guangling.zgl
         * @version $Id: Job.java, v 0.1 2019年10月28日 下午7:40:21 guangling.zgl Exp $
         */
    public enum Type {
        INQUIRY, CANCEL;
    }

    /** The time the Job is enabled to execute in nanoTime units */
    private long   time;
    private String paymentRequestId;
    private Type   type;

    /**
     * @param type 
     * @param paymentContext
     * @param delay
     * @param seconds
     */
    public Job(Type type, String paymentRequestId, int delay, TimeUnit unit) {
        this.paymentRequestId = paymentRequestId;
        this.time = triggerTime(delay, unit);
        this.type = type;
    }

    /**
     * Returns the trigger time of a delayed action.
     */
    private long triggerTime(long delay, TimeUnit unit) {
        return triggerTime(unit.toNanos((delay < 0) ? 0 : delay));
    }

    /**
     * Returns the trigger time of a delayed action.
     */
    long triggerTime(long delay) {
        return now() + delay;
    }

    /**
     * Returns current nanosecond time.
     */
    final long now() {
        return System.nanoTime();
    }

    /**
     * Getter method for property <tt>time</tt>.
     * 
     * @return property value of time
     */
    public long getTime() {
        return time;
    }

    /**
     * Setter method for property <tt>time</tt>.
     * 
     * @param time value to be assigned to property time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Getter method for property <tt>paymentRequestId</tt>.
     * 
     * @return property value of paymentRequestId
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    /**
     * Setter method for property <tt>paymentRequestId</tt>.
     * 
     * @param paymentRequestId value to be assigned to property paymentRequestId
     */
    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    /**
     * Getter method for property <tt>type</tt>.
     * 
     * @return property value of type
     */
    public Type getType() {
        return type;
    }

    /**
     * Setter method for property <tt>type</tt>.
     * 
     * @param type value to be assigned to property type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof Job)) {
            return false;
        }
        Job j = (Job) that;
        return this.getType().equals(j.getType())
               && this.getPaymentRequestId().equals(j.getPaymentRequestId())
               && this.getTime() == j.getTime();
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (getType().toString() + getPaymentRequestId() + getTime()).hashCode();
    }

}
