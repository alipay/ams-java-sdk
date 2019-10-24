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

/**
 * 
 * @author guangling.zgl
 * @version $Id: Request.java, v 0.1 2019年10月16日 上午11:44:14 guangling.zgl Exp $
 */
public abstract class Request extends AMSMessage {

    private RequestHeader requestHeader = new RequestHeader();
    private Signature     signature     = new Signature();

    protected Body        body          = new Body();

    public Request(String requestURI, AMSSettings settings) {
        super(requestURI, settings);
        requestHeader.setClientId(settings.clientId);
        requestHeader.setRequestTime(DateUtil.getISODateTimeStr(new Date()));
        requestHeader.setSignature(signature);

        requestHeader.setSdkVersion(getSdkVersion());
        requestHeader.setExt(getExt());

        requestHeader.setExtraHeaders(needExtraHeaders());
    }

    /**
     * 
     */
    protected void updateSignature() {
        signature.setSignature(SignatureUtil.sign(this, getSettings()));
    }

    public RequestHeader getRequestHeader() {
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
    public Body getBody() {
        return body;
    }

    protected abstract void updateBody();

    /**
     * Give sub-requests a chance to supply extra request headers.
     * 
     * @return
     */
    protected Map<String, String> needExtraHeaders() {
        return new HashMap<String, String>();
    }

    /**
     * extra validation defined by sub-requests
     * 
     * @return
     */
    protected abstract boolean extValidate();

    /**
     * 
     * @return
     */
    protected abstract String getExt();

    /**
     * 
     * @return
     */
    protected String getSdkVersion() {
        return "";
    }

}
