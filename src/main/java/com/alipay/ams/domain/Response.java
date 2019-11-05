/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;

import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Request.java, v 0.1 2019年10月16日 上午11:44:14 guangling.zgl Exp $
 */
public abstract class Response extends AMSMessage {

    private ResponseHeader responseHeader;

    /**
     * @param requestURI
     * @param settings 
     * @param responseHeader 
     * @param body 
     */
    protected Response(String requestURI, AMSSettings settings, ResponseHeader responseHeader,
                       HashMap<String, Object> body) {
        super(requestURI, settings);
        this.responseHeader = responseHeader;
        initBody(body, responseHeader);
    }

    /**
     * 
     */
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    /**
     * 
     * @param body
     * @param responseHeader
     */
    protected abstract void initBody(HashMap<String, Object> body, ResponseHeader responseHeader);

}
