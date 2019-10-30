/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.responses;

import java.util.Map;

/**
 * 
 * @author guangling.zgl
 * @version $Id: NotifyResponse.java, v 0.1 2019年10月30日 下午2:52:08 guangling.zgl Exp $
 */
public interface NotifyResponse {

    /**
     * Getter method for property <tt>bodyContent</tt>.
     * 
     * @return property value of bodyContent
     */
    public abstract byte[] getBodyContent();

    /**
     * Getter method for property <tt>responseHeaders</tt>.
     * 
     * @return property value of responseHeaders
     */
    public abstract Map<String, String> getResponseHeaders();

}