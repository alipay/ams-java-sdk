/*
 * The MIT License
 * Copyright © 2019 Alipay.com Inc.
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
