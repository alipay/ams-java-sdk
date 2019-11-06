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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.util.DateUtil;
import com.alipay.ams.util.SignatureUtil;
import com.alipay.ams.util.StringUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Request.java, v 0.1 2019年10月16日 上午11:44:14 guangling.zgl Exp $
 */
public abstract class Request extends AMSMessage {

    private String requestTime = DateUtil.getISODateTimeStr(new Date());

    public Request(String requestURI, AMSSettings settings) {
        super(requestURI, settings);
    }

    public RequestHeader buildRequestHeader() {

        RequestHeader requestHeader = new RequestHeader();

        requestHeader.setClientId(getSettings().clientId);
        requestHeader.setRequestTime(requestTime);
        requestHeader.setExtraHeaders(getExtraHeaders());

        if (StringUtil.isNotBlank(super.getAgentToken())) {
            requestHeader.setAgentToken(getAgentToken());
        }

        Signature signature = new Signature();
        requestHeader.setSignature(signature);

        //make sure this is the last call after everything is ready.
        if (getSettings().isDevMode()) {
            signature.setSignature("testing_signature");
        } else {
            signature.setSignature(SignatureUtil.sign(this, getSettings()));
        }

        return requestHeader;
    }

    public boolean validate() {
        //basic validation
        //        assert
        //signature
        return extValidate();
    }

    /** 
     */
    public abstract Body buildBody();

    /**
     * Give sub-requests a chance to supply extra request headers.
     * 
     * @return
     */
    protected Map<String, String> getExtraHeaders() {

        HashMap<String, String> headers = new HashMap<String, String>();

        if (getSettings().isDevMode()) {

            headers.put("original_host", "open-na.alipay.com");
        }

        return headers;
    }

    /**
     * extra validation defined by sub-requests
     * 
     * @return
     */
    protected abstract boolean extValidate();

    /**
     * Getter method for property <tt>requestTime</tt>.
     * 
     * @return property value of requestTime
     */
    public String getRequestTime() {
        return requestTime;
    }

    /**
     * Setter method for property <tt>requestTime</tt>.
     * 
     * @param requestTime value to be assigned to property requestTime
     */
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

}
