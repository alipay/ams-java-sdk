/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

/**
 * 
 * @author guangling.zgl
 * @version $Id: HeaderUtil.java, v 0.1 2019年10月23日 上午10:37:24 guangling.zgl Exp $
 */
public class HeaderUtil {

    /**
     * 
     * @param allHeaders
     * @return 
     */
    public static Map<String, String> toHeaderMap(Header[] allHeaders) {

        HashMap<String, String> hashMap = new HashMap<String, String>();

        for (Header h : allHeaders) {
            hashMap.put(h.getName().toLowerCase(), h.getValue());
        }

        return hashMap;
    }

    /**
     * 
     * @param header
     * @return
     */
    public static String getCharset(com.alipay.ams.domain.Header header) {

        return getCharset(header.getContentType());
    }

    /**
     * 
     * @param header
     * @return
     */
    public static String getCharset(String contentType) {

        return StringUtil.defaultIfEmpty(StringUtil.substringBetween(contentType, "charset=", ";"),
            StringUtil.defaultIfEmpty(StringUtil.substringAfter(contentType, "charset="), "UTF-8"))
            .trim();
    }
}
