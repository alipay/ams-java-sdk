/*
 * The MIT License
 * Copyright © 2019 Alipay
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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Request;

/**
 * 
 * @author guangling.zgl
 * @version $Id: SignatureUtil.java, v 0.1 2019年10月16日 上午11:27:42 guangling.zgl Exp $
 */
public class SignatureUtil {

    /**
     * 
     * @param request
     * @param settings
     * @return
     */
    public static String sign(Request request, AMSSettings settings) {
        return sign(request, settings.privateKey);
    }

    /**
     * 
     * @param request
     * @param privateKey
     * @return
     */
    public static String sign(Request request, String privateKey) {

        return sign(request.getRequestURI(), request.getSettings().clientId,
            request.getRequestTime(), privateKey, request.buildBody().toString());
    }

    /**
     * 
     * @param requestURI
     * @param clientId
     * @param requestTime
     * @param privateKey
     * @param requestBody
     * @return
     */
    public static String sign(String requestURI, String clientId, String requestTime,
                              String privateKey, String requestBody) {

        String content = String.format("POST %s\n%s.%s.%s", requestURI, clientId, requestTime,
            requestBody);

        try {
            java.security.Signature signature = java.security.Signature
                .getInstance("SHA256withRSA");

            PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes("UTF-8"))));

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();

            return URLEncoder.encode(new String(Base64.encodeBase64(signed), "UTF-8"), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param requestURI
     * @param clientId
     * @param reponseTime
     * @param alipayPublicKey
     * @param responseBody
     * @param signatureToBeVerified
     * @return
     */
    public static boolean verify(String requestURI, String clientId, String reponseTime,
                                 String alipayPublicKey, String responseBody,
                                 String signatureToBeVerified) {

        //signatureToBeVerified would not be present in the response when AMS returns a SIGNATURE_INVALID
        if (StringUtil.isBlank(signatureToBeVerified)) {
            return false;
        }

        String content = String.format("POST %s\n%s.%s.%s", requestURI, clientId, reponseTime,
            responseBody);

        try {
            java.security.Signature signature = java.security.Signature
                .getInstance("SHA256withRSA");

            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64.decodeBase64(alipayPublicKey.getBytes("UTF-8"))));

            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));

            return signature.verify(Base64.decodeBase64(URLDecoder.decode(signatureToBeVerified,
                "UTF-8").getBytes("UTF-8")));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
