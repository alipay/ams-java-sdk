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
package com.alipay.ams.callbacks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Currency;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import com.alipay.ams.AMSClient;
import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentRefundRequest;
import com.alipay.ams.domain.responses.PaymentRefundResponse;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentRefundCallbackTest.java, v 0.1 2020年4月23日 上午11:43:38 guangling.zgl Exp $
 */
public class PaymentRefundCallbackTest extends BaseAMSTest {

    @Test
    public void onIOException() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        PaymentRefundCallback paymentRefundCallback = spy(new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
            }
        });

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
            mockSetup.getAMSSettings(), "201911041940108001001882C0203027168",
            "201911041940108001001882C0203027168_refund1", new Amount(Currency.getInstance("JPY"),
                111l));
        mockSetup.getAmsClient().execute(paymentRefundRequest, paymentRefundCallback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/refund"));

        //3. request body content match
        assertTrue(Pattern.matches(new String(TestUtil.getApiMocks("/requests/refund.json"),
            "utf-8"), new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()),
            "utf-8")));

        verify(paymentRefundCallback, times(1)).onIOException(any(IOException.class),
            eq(mockSetup.getAmsClient()), eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(0)).onRefundSuccess(any(PaymentRefundResponse.class));
        verify(paymentRefundCallback, times(0)).onRefundFailure(anyString(),
            any(ResponseResult.class));

    }

    @Test
    public void onHttpStatusNot200() throws Exception {

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();

        PaymentRefundCallback paymentRefundCallback = spy(new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
            }
        });

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
            mockSetup.getAMSSettings(), "201911041940108001001882C0203027168",
            "201911041940108001001882C0203027168_refund1", new Amount(Currency.getInstance("JPY"),
                111l));
        mockSetup.getAmsClient().execute(paymentRefundRequest, paymentRefundCallback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/refund"));

        //3. request body content match
        assertTrue(Pattern.matches(new String(TestUtil.getApiMocks("/requests/refund.json"),
            "utf-8"), new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()),
            "utf-8")));

        verify(paymentRefundCallback, times(0)).onIOException(any(IOException.class),
            eq(mockSetup.getAmsClient()), eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(1)).onHttpStatusNot200(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), anyInt());

        verify(paymentRefundCallback, times(0)).onRefundSuccess(any(PaymentRefundResponse.class));
        verify(paymentRefundCallback, times(0)).onRefundFailure(anyString(),
            any(ResponseResult.class));

    }

    @Test
    public void onFstatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_f.json");

        PaymentRefundCallback paymentRefundCallback = spy(new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
            }
        });

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
            mockSetup.getAMSSettings(), "201911041940108001001882C0203027168",
            "201911041940108001001882C0203027168_refund1", new Amount(Currency.getInstance("JPY"),
                111l));
        mockSetup.getAmsClient().execute(paymentRefundRequest, paymentRefundCallback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/refund"));

        //3. request body content match
        assertTrue(Pattern.matches(new String(TestUtil.getApiMocks("/requests/refund.json"),
            "utf-8"), new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()),
            "utf-8")));

        verify(paymentRefundCallback, times(0)).onIOException(any(IOException.class),
            eq(mockSetup.getAmsClient()), eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(0)).onHttpStatusNot200(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), anyInt());

        verify(paymentRefundCallback, times(1)).onFstatus(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), any(ResponseResult.class));

        verify(paymentRefundCallback, times(0)).onRefundSuccess(any(PaymentRefundResponse.class));
        verify(paymentRefundCallback, times(1)).onRefundFailure(anyString(),
            any(ResponseResult.class));

    }

    @Test
    public void onUstatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_u.json");

        PaymentRefundCallback paymentRefundCallback = spy(new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
            }
        });

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
            mockSetup.getAMSSettings(), "201911041940108001001882C0203027168",
            "201911041940108001001882C0203027168_refund1", new Amount(Currency.getInstance("JPY"),
                111l));
        mockSetup.getAmsClient().execute(paymentRefundRequest, paymentRefundCallback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/refund"));

        //3. request body content match
        assertTrue(Pattern.matches(new String(TestUtil.getApiMocks("/requests/refund.json"),
            "utf-8"), new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()),
            "utf-8")));

        verify(paymentRefundCallback, times(0)).onIOException(any(IOException.class),
            eq(mockSetup.getAmsClient()), eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(0)).onHttpStatusNot200(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), anyInt());

        verify(paymentRefundCallback, times(0)).onFstatus(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), any(ResponseResult.class));

        verify(paymentRefundCallback, times(1)).onUstatus(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), any(ResponseResult.class));

        verify(paymentRefundCallback, times(0)).onRefundSuccess(any(PaymentRefundResponse.class));
        verify(paymentRefundCallback, times(0)).onRefundFailure(anyString(),
            any(ResponseResult.class));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void onSstatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/refund_ss.json");

        PaymentRefundCallback paymentRefundCallback = spy(new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
            }
        });

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
            mockSetup.getAMSSettings(), "201911041940108001001882C0203027168",
            "201911041940108001001882C0203027168_refund1", new Amount(Currency.getInstance("JPY"),
                111l));
        mockSetup.getAmsClient().execute(paymentRefundRequest, paymentRefundCallback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/refund"));

        //3. request body content match
        assertTrue(Pattern.matches(new String(TestUtil.getApiMocks("/requests/refund.json"),
            "utf-8"), new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()),
            "utf-8")));

        verify(paymentRefundCallback, times(0)).onIOException(any(IOException.class),
            eq(mockSetup.getAmsClient()), eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(0)).onHttpStatusNot200(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), anyInt());

        verify(paymentRefundCallback, times(0)).onFstatus(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), any(ResponseResult.class));

        verify(paymentRefundCallback, times(0)).onUstatus(eq(mockSetup.getAmsClient()),
            eq(paymentRefundRequest), any(ResponseResult.class));

        verify(paymentRefundCallback, times(1)).onSstatus(eq(mockSetup.getAmsClient()),
            anyString(), any(ResponseHeader.class), any(java.util.HashMap.class),
            eq(paymentRefundRequest));

        verify(paymentRefundCallback, times(1)).onRefundSuccess(any(PaymentRefundResponse.class));
        verify(paymentRefundCallback, times(0)).onRefundFailure(anyString(),
            any(ResponseResult.class));

    }
}
