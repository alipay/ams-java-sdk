/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.responses;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.NotifyResponseHeader;
import com.alipay.ams.domain.Signature;
import com.alipay.ams.util.DateUtil;
import com.alipay.ams.util.SignatureUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyResponseImpl.java, v 0.1 2019年10月23日 上午11:05:26 guangling.zgl Exp $
 */
public class NotifyResponseImpl implements NotifyResponse {

    public final static String   S       = "{\"result\":{\"resultCode\":\"SUCCESS\",\"resultStatus\":\"S\",\"resultMessage\":\"success\"}}";
    public final static String   U       = "{\"result\":{\"resultCode\":\"UNKNOWN_EXCEPTION\",\"resultStatus\":\"U\",\"resultMessage\":\"UNKNOWN_EXCEPTION\"}}";

    private NotifyResponseHeader headers = new NotifyResponseHeader();
    private String               bodyContent;
    private AMSSettings          setting;
    private String               requestURI;

    public NotifyResponseImpl(AMSSettings setting, String requestURI, String bodyContent) {

        this.setting = setting;
        this.bodyContent = bodyContent;
        this.requestURI = requestURI;

        headers.setContentType("application/json; charset=UTF-8");
        headers.setClientId(setting.clientId);
        headers.setResponseTime(DateUtil.getISODateTimeStr(new Date()));

    }

    /** 
     * @see com.alipay.ams.domain.responses.NotifyResponse#getBodyContent()
     */
    @Override
    public byte[] getBodyContent() {

        try {
            return bodyContent.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            setting.logger.warn("UnsupportedEncodingException: UTF-8, bodyContent=[%s]",
                bodyContent);
            return null;
        }
    }

    /** 
     * @see com.alipay.ams.domain.responses.NotifyResponse#getResponseHeaders()
     */
    @Override
    public Map<String, String> getResponseHeaders() {

        Signature signature = new Signature();

        if (setting.isDevMode()) {
            signature.setSignature("testing_signature");
        } else {
            signature.setSignature(SignatureUtil.sign(requestURI, setting.clientId,
                headers.getResponseTime(), setting.privateKey, bodyContent));
        }

        headers.setSignature(signature);
        return headers.toMap();
    }

}
