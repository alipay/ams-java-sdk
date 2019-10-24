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
     * @param responseHeader
     * @param responseBody
     */
    public void onSignatureVerifyFailed(ResponseHeader responseHeader, String responseBody) {
        //log and report
        System.out.println("onSignatureVerifyFailed");
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
