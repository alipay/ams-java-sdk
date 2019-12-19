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
package com.alipay.ams.domain.telemetry;

import java.io.Serializable;

import com.alipay.ams.domain.ResultStatusType;

/**
 * 
 * Record one API statistics.
 * 
 * @author guangling.zgl
 * @version $Id: Call.java, v 0.1 2019年11月4日 下午3:47:35 guangling.zgl Exp $
 */
public abstract class Call implements Serializable {
    /**  */
    private static final long serialVersionUID = 3445578766232853266L;

    private String            api;

    private ResultStatusType  resultStatusType;

    /**
     * @param api
     */
    public Call(String api) {
        super();
        this.api = api;
    }

    private long requestStartMs;
    private long responseReceivedMs;
    private long ioExceptionOrHttpStatusNot200ReceivedMs;

    /**
     * 
     */
    public boolean isFinished() {

        return (requestStartMs != 0 && ioExceptionOrHttpStatusNot200ReceivedMs != 0)
               || (requestStartMs != 0 && responseReceivedMs != 0 && resultStatusType != null);

    }

    /**
     * 
     */
    public long getRequestDurationMs() {
        return responseReceivedMs == 0 ? ioExceptionOrHttpStatusNot200ReceivedMs - requestStartMs
            : responseReceivedMs - requestStartMs;
    }

    /**
     * 
     * @param resultStatusType
     */
    public void setResultStatusType(ResultStatusType resultStatusType) {
        this.resultStatusType = resultStatusType;
    }

    /**
     * Getter method for property <tt>resultStatusType</tt>.
     * 
     * @return property value of resultStatusType
     */
    public ResultStatusType getResultStatusType() {
        return resultStatusType;
    }

    /**
     * Getter method for property <tt>api</tt>.
     * 
     * @return property value of api
     */
    public String getApi() {
        return api;
    }

    /**
     * Setter method for property <tt>api</tt>.
     * 
     * @param api value to be assigned to property api
     */
    public void setApi(String api) {
        this.api = api;
    }

    /**
     * Getter method for property <tt>requestStartMs</tt>.
     * 
     * @return property value of requestStartMs
     */
    public long getRequestStartMs() {
        return requestStartMs;
    }

    /**
     * Setter method for property <tt>requestStartMs</tt>.
     * 
     * @param requestStartMs value to be assigned to property requestStartMs
     */
    public void setRequestStartMs(long requestStartMs) {
        this.requestStartMs = requestStartMs;
    }

    /**
     * Getter method for property <tt>responseReceivedMs</tt>.
     * 
     * @return property value of responseReceivedMs
     */
    public long getResponseReceivedMs() {
        return responseReceivedMs;
    }

    /**
     * Setter method for property <tt>responseReceivedMs</tt>.
     * 
     * @param responseReceivedMs value to be assigned to property responseReceivedMs
     */
    public void setResponseReceivedMs(long responseReceivedMs) {
        this.responseReceivedMs = responseReceivedMs;
    }

    /**
     * Getter method for property <tt>ioExceptionOrHttpStatusNot200ReceivedMs</tt>.
     * 
     * @return property value of ioExceptionOrHttpStatusNot200ReceivedMs
     */
    public long getIoExceptionOrHttpStatusNot200ReceivedMs() {
        return ioExceptionOrHttpStatusNot200ReceivedMs;
    }

    /**
     * Setter method for property <tt>ioExceptionOrHttpStatusNot200ReceivedMs</tt>.
     * 
     * @param ioExceptionOrHttpStatusNot200ReceivedMs value to be assigned to property ioExceptionOrHttpStatusNot200ReceivedMs
     */
    public void setIoExceptionOrHttpStatusNot200ReceivedMs(long ioExceptionOrHttpStatusNot200ReceivedMs) {
        this.ioExceptionOrHttpStatusNot200ReceivedMs = ioExceptionOrHttpStatusNot200ReceivedMs;
    }

}
