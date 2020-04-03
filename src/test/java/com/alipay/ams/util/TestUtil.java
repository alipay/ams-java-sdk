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
package com.alipay.ams.util;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;

/**
 * 
 * @author guangling.zgl
 * @version $Id: TestUtil.java, v 0.1 2020年4月3日 下午4:54:13 guangling.zgl Exp $
 */
public class TestUtil {

    /**
     * 
     * @param requestUsed
     */
    public static void assertRequestHeaders(HttpPost requestUsed) {
        Header[] allHeaders = requestUsed.getAllHeaders();
        //test request headers set        
        assertTrue("signature missing", containsHeader(allHeaders, "signature"));
        assertTrue("request-time missing", containsHeader(allHeaders, "request-time"));
        assertTrue("X-sdkVersion missing", containsHeader(allHeaders, "X-sdkVersion"));
        assertTrue("client-id missing", containsHeader(allHeaders, "client-id"));
        assertTrue("Content-Type missing", containsHeader(allHeaders, "Content-Type"));
    }

    public static byte[] getApiMocks(String filePath) throws Exception {

        URI uri = TestUtil.class.getClassLoader().getResource("api_mocks" + filePath).toURI();

        return Files.readAllBytes(Paths.get(uri));
    }

    /**
     * 
     * @param allHeaders
     * @param headerName
     * @return
     */
    private static boolean containsHeader(Header[] allHeaders, String headerName) {
        for (Header h : allHeaders) {
            if (h.getName().equals(headerName)) {
                return true;
            }
        }
        return false;
    }

}
