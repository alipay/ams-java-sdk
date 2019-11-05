/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
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
