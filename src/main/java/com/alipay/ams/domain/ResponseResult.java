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

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author guangling.zgl
 * @version $Id: ResponseResult.java, v 0.1 2019年10月17日 下午4:36:00 guangling.zgl Exp $
 */
public class ResponseResult {

    private String           resultCode;
    private ResultStatusType resultStatus;
    private String           resultMessage;

    public static ResponseResult fromMap(Map<String, String> map) {
        return new ResponseResult(map.get("resultCode"), ResultStatusType.valueOf(map
            .get("resultStatus")), map.get("resultMessage"));
    }

    /**
     * @param resultCode
     * @param resultStatus
     * @param resultMessage
     */
    public ResponseResult(String resultCode, ResultStatusType resultStatus, String resultMessage) {
        super();
        this.resultCode = resultCode;
        this.resultStatus = resultStatus;
        this.resultMessage = resultMessage;
    }

    /**
     * Getter method for property <tt>resultCode</tt>.
     * 
     * @return property value of resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Setter method for property <tt>resultCode</tt>.
     * 
     * @param resultCode value to be assigned to property resultCode
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Getter method for property <tt>resultStatus</tt>.
     * 
     * @return property value of resultStatus
     */
    public ResultStatusType getResultStatus() {
        return resultStatus;
    }

    /**
     * Setter method for property <tt>resultStatus</tt>.
     * 
     * @param resultStatus value to be assigned to property resultStatus
     */
    public void setResultStatus(ResultStatusType resultStatus) {
        this.resultStatus = resultStatus;
    }

    /**
     * Getter method for property <tt>resultMessage</tt>.
     * 
     * @return property value of resultMessage
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * Setter method for property <tt>resultMessage</tt>.
     * 
     * @param resultMessage value to be assigned to property resultMessage
     */
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
