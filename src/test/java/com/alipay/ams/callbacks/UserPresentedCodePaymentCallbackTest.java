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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Currency;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.alipay.ams.ApacheHttpPostAMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.domain.responses.UserPresentedCodePaymentResponse;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: UserPresentedCodePaymentCallbackTest.java, v 0.1 2020年4月3日 下午4:46:23 guangling.zgl Exp $
 */
public class UserPresentedCodePaymentCallbackTest {
    @Test
    public void onIOException() throws Exception {

        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<HttpPost> argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mock(TelemetrySupport.class);
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mock(TelemetrySupport.class);

        doThrow(new IOException()).when(closeableHttpClient).execute(argumentCaptor.capture());

        AMSSettings cfg = new AMSSettings();
        ApacheHttpPostAMSClient amsClient = new ApacheHttpPostAMSClient(cfg);
        amsClient.setHttpClient(closeableHttpClient);

        final UserPresentedCodePaymentRequest request = newUPCPRequest(cfg);

        PaymentInquiryCallback paymentInquiryCallback = mock(PaymentInquiryCallback.class);
        PaymentCancelCallback paymentCancelCallback = mock(PaymentCancelCallback.class);
        doReturn(paymentCancelCallback).when(paymentInquiryCallback).getPaymentCancelCallback();
        doReturn(telemetrySupportPaymentInquiry).when(paymentInquiryCallback).getTelemetrySupport();

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            paymentInquiryCallback);
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        amsClient.execute(request, callback);
        amsClient.close();

        //1. 2 requests

        List<HttpPost> requestsUsed = argumentCaptor.getAllValues();
        assertEquals(2, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));
        TestUtil.assertRequestHeaders(requestsUsed.get(1));

        assertTrue("pay uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));
        assertTrue("inquiryPayment uri not expected", requestsUsed.get(1).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/upcp.json"), "utf-8"), new String(
            IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));
        assertEquals("{\"paymentRequestId\":\"PR20190000000001_\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(1).getEntity().getContent())));

        //4. reportRequestStart        
        verify(telemetrySupportPaymentUPCP, times(1)).reportRequestStart(
            any(UserPresentedCodePaymentRequest.class), any(RequestHeader.class));

        //5. no reportResponseReceived
        verify(telemetrySupportPaymentUPCP, never()).reportResponseReceived(
            any(UserPresentedCodePaymentRequest.class));

        //6. reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentUPCP, times(1)).reportRequestIOExceptionOrHttpStatusNot200(
            any(UserPresentedCodePaymentRequest.class));

        //7. reportRequestStart of inquiry      
        verify(telemetrySupportPaymentInquiry, times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(telemetrySupportPaymentInquiry, never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentInquiry, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

    }

    @Test
    public void testHttpStatusNot200() throws Exception {

        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<HttpPost> argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mock(TelemetrySupport.class);
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mock(TelemetrySupport.class);
        CloseableHttpResponse closeableHttpResponse = mock(CloseableHttpResponse.class);

        doReturn(closeableHttpResponse).when(closeableHttpClient).execute(argumentCaptor.capture());
        StatusLine statusLine = new StatusLine() {

            @Override
            public int getStatusCode() {
                return 300;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }
        };
        doReturn(statusLine).when(closeableHttpResponse).getStatusLine();

        AMSSettings cfg = new AMSSettings();
        ApacheHttpPostAMSClient amsClient = new ApacheHttpPostAMSClient(cfg);
        amsClient.setHttpClient(closeableHttpClient);

        final UserPresentedCodePaymentRequest request = newUPCPRequest(cfg);

        PaymentInquiryCallback paymentInquiryCallback = mock(PaymentInquiryCallback.class);
        PaymentCancelCallback paymentCancelCallback = mock(PaymentCancelCallback.class);
        doReturn(paymentCancelCallback).when(paymentInquiryCallback).getPaymentCancelCallback();
        doReturn(telemetrySupportPaymentInquiry).when(paymentInquiryCallback).getTelemetrySupport();

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            paymentInquiryCallback);
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        amsClient.execute(request, callback);
        amsClient.close();

        //1. 2 requests

        List<HttpPost> requestsUsed = argumentCaptor.getAllValues();
        assertEquals(2, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));
        TestUtil.assertRequestHeaders(requestsUsed.get(1));

        assertTrue("pay uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));
        assertTrue("inquiryPayment uri not expected", requestsUsed.get(1).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/upcp.json"), "utf-8"), new String(
            IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));
        assertEquals("{\"paymentRequestId\":\"PR20190000000001_\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(1).getEntity().getContent())));

        //4. reportRequestStart        
        verify(telemetrySupportPaymentUPCP, times(1)).reportRequestStart(
            any(UserPresentedCodePaymentRequest.class), any(RequestHeader.class));

        //5. reportResponseReceived
        verify(telemetrySupportPaymentUPCP, times(1)).reportResponseReceived(
            any(UserPresentedCodePaymentRequest.class));

        //6. reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentUPCP, times(1)).reportRequestIOExceptionOrHttpStatusNot200(
            any(UserPresentedCodePaymentRequest.class));

        //7. reportRequestStart of inquiry      
        verify(telemetrySupportPaymentInquiry, times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. reportResponseReceived
        verify(telemetrySupportPaymentInquiry, times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentInquiry, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

    }

    @Test
    public void testFStatusResponse() throws Exception {

        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<HttpPost> argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mock(TelemetrySupport.class);
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mock(TelemetrySupport.class);
        CloseableHttpResponse closeableHttpResponse = mock(CloseableHttpResponse.class);

        doReturn(closeableHttpResponse).when(closeableHttpClient).execute(argumentCaptor.capture());
        StatusLine statusLine = new StatusLine() {

            @Override
            public int getStatusCode() {
                return 200;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }
        };
        doReturn(statusLine).when(closeableHttpResponse).getStatusLine();

        HttpEntity entity = mock(HttpEntity.class);
        doReturn(entity).when(closeableHttpResponse).getEntity();
        InputStream inputStream = new ByteArrayInputStream(
            TestUtil.getApiMocks("/responses/upcp_f.json"));
        doReturn(inputStream).when(entity).getContent();

        Header[] responseHeaders = new Header[0];
        doReturn(responseHeaders).when(closeableHttpResponse).getAllHeaders();

        AMSSettings cfg = new AMSSettings() {
            /** 
             * @see com.alipay.ams.cfg.AMSSettings#isDevMode()
             */
            @Override
            public boolean isDevMode() {
                return true;
            }
        };

        ApacheHttpPostAMSClient amsClient = new ApacheHttpPostAMSClient(cfg);
        amsClient.setHttpClient(closeableHttpClient);

        final UserPresentedCodePaymentRequest request = newUPCPRequest(cfg);

        PaymentInquiryCallback paymentInquiryCallback = mock(PaymentInquiryCallback.class);
        PaymentCancelCallback paymentCancelCallback = mock(PaymentCancelCallback.class);
        PaymentStatusUpdateCallback paymentStatusUpdateCallback = mock(PaymentStatusUpdateCallback.class);
        doReturn(paymentCancelCallback).when(paymentInquiryCallback).getPaymentCancelCallback();
        doReturn(telemetrySupportPaymentInquiry).when(paymentInquiryCallback).getTelemetrySupport();
        doReturn(paymentStatusUpdateCallback).when(paymentCancelCallback)
            .getPaymentStatusUpdateCallback();

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            paymentInquiryCallback);
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        amsClient.execute(request, callback);
        amsClient.close();

        //1. 1 requests

        List<HttpPost> requestsUsed = argumentCaptor.getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("pay uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/upcp.json"), "utf-8"), new String(
            IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));

        //4. reportRequestStart        
        verify(telemetrySupportPaymentUPCP, times(1)).reportRequestStart(
            any(UserPresentedCodePaymentRequest.class), any(RequestHeader.class));

        //5. reportResponseReceived
        verify(telemetrySupportPaymentUPCP, times(1)).reportResponseReceived(
            any(UserPresentedCodePaymentRequest.class));

        //6. reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentUPCP, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(UserPresentedCodePaymentRequest.class));

        //7. no reportRequestStart of inquiry      
        verify(telemetrySupportPaymentInquiry, never()).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived of inquiry  
        verify(telemetrySupportPaymentInquiry, never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentInquiry, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

        //10. onPaymentFailed
        verify(paymentStatusUpdateCallback, times(1)).onPaymentFailed(any(String.class),
            any(ResponseResult.class));

        //11. reportPaymentF
        verify(telemetrySupportPaymentUPCP, times(1)).reportPaymentF(any(String.class));

    }

    /**
     * 
     * @param cfg
     * @return
     */
    private UserPresentedCodePaymentRequest newUPCPRequest(AMSSettings cfg) {

        long amountInCents = 1000l;
        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, amountInCents));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_";
        String buyerPaymentCode = "288888888888888888";

        return new UserPresentedCodePaymentRequest(cfg, paymentRequestId, order, currency,
            amountInCents, buyerPaymentCode);
    }
}
