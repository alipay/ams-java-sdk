/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.Request;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.Response;
import com.alipay.ams.util.HeaderUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: ApacheHttpPostAMSClient.java, v 0.1 2019年10月16日 下午8:10:37 guangling.zgl Exp $
 */
@SuppressWarnings("deprecation")
public class ApacheHttpPostAMSClient extends AMSClient {

    /**
     * @param cfg
     */
    public ApacheHttpPostAMSClient(AMSSettings cfg) {
        super(cfg);
    }

    /** 
     * @see com.alipay.ams.AMSClient#execute(com.alipay.ams.domain.Request, com.alipay.ams.domain.Callback)
     */
    @Override
    public <R extends Request, P extends Response> void execute(R request, Callback<R, P> callback) {

        HttpPost httpPost = null;
        CloseableHttpResponse response = null;

        try {

            httpPost = new HttpPost(request.getSettings().gatewayUrl + request.getRequestURI());

            String body = request.buildBody().toString();
            RequestHeader requestHeader = request.buildRequestHeader();

            httpPost.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));

            callback.reportRequestStart(request, requestHeader);

            getSettings().logger.debug("Request[%s][%s]: body->[%s], header->[%s]",
                request.getBizIdentifier(),
                request.getSettings().gatewayUrl + request.getRequestURI(), body, requestHeader);

            for (Entry<String, String> e : requestHeader.toMap().entrySet()) {
                httpPost.setHeader(e.getKey(), e.getValue());
            }

            response = new DefaultHttpClient().execute(httpPost);

            callback.reportResponseReceived(request);

            switch (response.getStatusLine().getStatusCode()) {

                case HttpStatus.SC_OK:

                    byte[] byteArray = IOUtils.toByteArray(response.getEntity().getContent());
                    Map<String, String> headerMap = HeaderUtil
                        .toHeaderMap(response.getAllHeaders());

                    super.onHttpStatus200(request, callback, byteArray, headerMap);

                    break;

                default:
                    getSettings().logger.warn("Response[%s][%s]: onHttpStatusNot200: [%s]", request
                        .getBizIdentifier(),
                        request.getSettings().gatewayUrl + request.getRequestURI(), response
                            .getStatusLine().getStatusCode());
                    super.onHttpStatusNot200(callback, request, response.getStatusLine()
                        .getStatusCode());
            }

        } catch (IOException e) {

            super.onIOException(callback, request, e);

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }
}
