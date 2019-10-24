/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Body.java, v 0.1 2019年10月16日 上午11:46:35 guangling.zgl Exp $
 */
public class Body {

    private Map<String, Object> payload = new HashMap<String, Object>();

    public void put(String key, Object value) {
        payload.put(key, value);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(payload);
    }
}
