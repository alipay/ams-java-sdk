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
package com.alipay.ams;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

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
public class ApacheHttpPostAMSClient extends AMSClient implements Closeable {

    private CloseableHttpClient httpClient;

    /**
     * @param cfg
     */
    public ApacheHttpPostAMSClient(AMSSettings cfg) {
        super(cfg);

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(cfg.apacheMaxPoolSize);

        httpClient = HttpClients
            .custom()
            .setConnectionManager(connManager)
            .setDefaultRequestConfig(
                RequestConfig.copy(RequestConfig.DEFAULT)
                    .setSocketTimeout(cfg.apacheHttpSocketTimeout)
                    .setConnectTimeout(cfg.apacheHttpConnectTimeout)
                    .setConnectionRequestTimeout(cfg.apacheHttpConnectionRequestTimeout).build())
            .build();
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

            response = httpClient.execute(httpPost);

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

    /** 
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
