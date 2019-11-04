/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Callback.java, v 0.1 2019年10月16日 下午7:48:32 guangling.zgl Exp $
 */
public abstract class Callback<R extends Request, P extends Response> {

    /**
     * 
     * @param e
     * @param client
     * @param request
     */
    public abstract void onIOException(IOException e, AMSClient client, R request);

    /**
     * 
     * @param code
     */
    public abstract void onHttpStatusNot200(AMSClient client, R request, int code);

    /**
     * 
     * @param request 
     * @param responseHeader
     * @param responseBody
     */
    public void onSignatureVerifyFailed(R request, ResponseHeader responseHeader,
                                        String responseBody) {
        throw new IllegalStateException(String.format(
            "SignatureVerifyFailed[%s]: %s[bizId=[%s]], responseBody=[%s]", request.getClass()
                .getName(), request.getBizIdentifier(), responseBody));
    }

    /**
     * 
     * @param responseResult
     */
    public abstract void onFstatus(AMSClient client, R request, ResponseResult responseResult);

    /**
     * 
     * @param responseResult
     * @param client
     * @param request
     */
    public abstract void onUstatus(AMSClient client, R request, ResponseResult responseResult);

    /**
     * 
     * @param settings
     * @param requestURI
     * @param responseHeader
     * @param body
     */
    public abstract void onSstatus(AMSClient client, String requestURI,
                                   ResponseHeader responseHeader, HashMap<String, Object> body,
                                   R request);

}
