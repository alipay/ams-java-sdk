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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import com.alipay.ams.ApacheHttpPostAMSClient;
import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Env;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.NotifyCallback;
import com.alipay.ams.domain.NotifyRequestHeader;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.NotifyResponse;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.domain.responses.UserPresentedCodePaymentResponse;
import com.alipay.ams.util.Logger;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: UserPresentedCodePaymentCallbackTest.java, v 0.1 2020年4月3日 下午4:46:23 guangling.zgl Exp $
 */
@SuppressWarnings("unchecked")
public class UserPresentedCodePaymentCallbackTest extends BaseAMSTest {
    @Test
    public void onIOException() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mockSetup
            .getTelemetrySupportPaymentUPCP();
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mockSetup
            .getTelemetrySupportPaymentInquiry();

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            mockSetup.getPaymentInquiryCallback());
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);

        final UserPresentedCodePaymentRequest request = newUPCPRequest(mockSetup.getAMSSettings());
        mockSetup.getAmsClient().execute(request, callback);
        mockSetup.getAmsClient().close();

        //1. 2 requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
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

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mockSetup
            .getTelemetrySupportPaymentUPCP();
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mockSetup
            .getTelemetrySupportPaymentInquiry();

        final UserPresentedCodePaymentRequest request = newUPCPRequest(mockSetup.getAMSSettings());

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            mockSetup.getPaymentInquiryCallback());
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        mockSetup.getAmsClient().execute(request, callback);
        mockSetup.getAmsClient().close();

        //1. 2 requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
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

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_f.json");
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mockSetup
            .getTelemetrySupportPaymentUPCP();
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mockSetup
            .getTelemetrySupportPaymentInquiry();

        final UserPresentedCodePaymentRequest request = newUPCPRequest(mockSetup.getAMSSettings());

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            mockSetup.getPaymentInquiryCallback());
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        mockSetup.getAmsClient().execute(request, callback);
        mockSetup.getAmsClient().close();

        //1. One requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
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
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).onPaymentFailed(
            any(String.class), any(ResponseResult.class));

        //11. reportPaymentF
        verify(telemetrySupportPaymentUPCP, times(1)).reportPaymentF(any(String.class));

    }

    @Test
    public void testUStatusResponse() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_u.json",
            "/responses/upcp_u.json");
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mockSetup
            .getTelemetrySupportPaymentUPCP();
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mockSetup
            .getTelemetrySupportPaymentInquiry();

        final UserPresentedCodePaymentRequest request = newUPCPRequest(mockSetup.getAMSSettings());

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            mockSetup.getPaymentInquiryCallback());
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        mockSetup.getAmsClient().execute(request, callback);
        mockSetup.getAmsClient().close();

        //1. two requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
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
        verify(telemetrySupportPaymentUPCP, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(UserPresentedCodePaymentRequest.class));

        //7. reportRequestStart of inquiry      
        verify(telemetrySupportPaymentInquiry, times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. reportResponseReceived of inquiry  
        verify(telemetrySupportPaymentInquiry, times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. No reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentInquiry, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

        //10. No onPaymentFailed
        verify(mockSetup.getPaymentStatusUpdateCallback(), never()).onPaymentFailed(
            any(String.class), any(ResponseResult.class));

        //11. No reportPaymentF
        verify(telemetrySupportPaymentUPCP, never()).reportPaymentF(any(String.class));

    }

    @Test
    public void testSStatusResponse() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_s.json");
        TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP = mockSetup
            .getTelemetrySupportPaymentUPCP();
        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry = mockSetup
            .getTelemetrySupportPaymentInquiry();

        final UserPresentedCodePaymentRequest request = newUPCPRequest(mockSetup.getAMSSettings());

        UserPresentedCodePaymentCallback callback = new UserPresentedCodePaymentCallback(
            mockSetup.getPaymentInquiryCallback());
        callback.setTelemetrySupport(telemetrySupportPaymentUPCP);
        mockSetup.getAmsClient().execute(request, callback);
        mockSetup.getAmsClient().close();

        //1. 1 requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
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

        //6. No reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentUPCP, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(UserPresentedCodePaymentRequest.class));

        //7. No reportRequestStart of inquiry      
        verify(telemetrySupportPaymentInquiry, never()).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. No reportResponseReceived of inquiry  
        verify(telemetrySupportPaymentInquiry, never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. No reportRequestIOExceptionOrHttpStatusNot200        
        verify(telemetrySupportPaymentInquiry, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

        //10. No onPaymentFailed
        verify(mockSetup.getPaymentStatusUpdateCallback(), never()).onPaymentFailed(
            any(String.class), any(ResponseResult.class));

        //11. No reportPaymentF
        verify(telemetrySupportPaymentUPCP, never()).reportPaymentF(any(String.class));

        //12. onPaymentSuccess
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));

        //13. reportPaymentS
        verify(telemetrySupportPaymentUPCP, times(1)).reportPaymentS(any(String.class));

    }

    @Test
    public void testOnNotifyException() throws Exception {

        AMSSettings cfg = new AMSSettings() {
            {
                super.logger = new Logger() {

                    @Override
                    public void warn(String format, Object... args) {
                    }

                    @Override
                    public void info(String format, Object... args) {
                    }

                    @Override
                    public void debug(String format, Object... args) {
                    }
                };
            }
        };

        ApacheHttpPostAMSClient amsClient = new ApacheHttpPostAMSClient(cfg);
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        notifyRequestheaders.put("content-type", "charset=some_incorrect_charset");
        byte[] bodyContent = TestUtil.getApiMocks("/requests/notify_s.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"UNKNOWN_EXCEPTION\",\"resultStatus\":\"U\",\"resultMessage\":\"UNKNOWN_EXCEPTION\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

    }

    @Test
    public void testOnNotifySignatureVerifyFailed() throws Exception {

        AMSSettings cfg = new AMSSettings();

        ApacheHttpPostAMSClient amsClient = new ApacheHttpPostAMSClient(cfg);
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        byte[] bodyContent = TestUtil.getApiMocks("/requests/notify_s.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"UNKNOWN_EXCEPTION\",\"resultStatus\":\"U\",\"resultMessage\":\"UNKNOWN_EXCEPTION\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

        verify(notifyCallback, times(1)).onNotifySignatureVerifyFailed(
            any(NotifyRequestHeader.class), any(String.class));

    }

    @Test
    public void testOnNotifySStatus() throws Exception {

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
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        byte[] bodyContent = TestUtil.getApiMocks("/requests/notify_s.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"SUCCESS\",\"resultStatus\":\"S\",\"resultMessage\":\"success\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

        verify(notifyCallback, never()).onNotifySignatureVerifyFailed(
            any(NotifyRequestHeader.class), any(String.class));

        verify(notifyCallback, times(1)).onSstatus(eq(cfg), any(NotifyRequestHeader.class),
            any(HashMap.class));

    }

    @Test
    public void testOnNotifyUStatus() throws Exception {

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
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        byte[] bodyContent = TestUtil.getApiMocks("/responses/upcp_u.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"SUCCESS\",\"resultStatus\":\"S\",\"resultMessage\":\"success\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

        verify(notifyCallback, never()).onNotifySignatureVerifyFailed(
            any(NotifyRequestHeader.class), any(String.class));

        verify(notifyCallback, never()).onSstatus(eq(cfg), any(NotifyRequestHeader.class),
            any(HashMap.class));

        verify(notifyCallback, times(1)).onUstatus(any(ResponseResult.class));

    }

    @Test
    public void testOnNotifyFStatus() throws Exception {

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
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        byte[] bodyContent = TestUtil.getApiMocks("/responses/upcp_f.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"SUCCESS\",\"resultStatus\":\"S\",\"resultMessage\":\"success\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

        verify(notifyCallback, never()).onNotifySignatureVerifyFailed(
            any(NotifyRequestHeader.class), any(String.class));

        verify(notifyCallback, never()).onSstatus(eq(cfg), any(NotifyRequestHeader.class),
            any(HashMap.class));

        verify(notifyCallback, times(1)).onFstatus(any(ResponseResult.class));

    }

    @Test
    public void testOnAuthNotify() throws Exception {

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
        Map<String, String> notifyRequestheaders = new HashMap<String, String>();
        byte[] bodyContent = TestUtil.getApiMocks("/requests/notify_auth.json");
        NotifyCallback notifyCallback = mock(NotifyCallback.class);

        NotifyResponse notifyResponse = amsClient.onNotify("/some_notify_endpoint",
            notifyRequestheaders, bodyContent, notifyCallback);
        amsClient.close();

        assertEquals(
            "{\"result\":{\"resultCode\":\"SUCCESS\",\"resultStatus\":\"S\",\"resultMessage\":\"success\"}}",
            new String(notifyResponse.getBodyContent(), "utf-8"));

        assertEquals(5, notifyResponse.getResponseHeaders().size());

        verify(notifyCallback, never()).onNotifySignatureVerifyFailed(
            any(NotifyRequestHeader.class), any(String.class));

        verify(notifyCallback, never()).onSstatus(eq(cfg), any(NotifyRequestHeader.class),
            any(HashMap.class));

        verify(notifyCallback, times(1)).onNonPaymentNotify(eq(cfg),
            any(NotifyRequestHeader.class), any(HashMap.class));

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

        order.setEnv(new Env());
        order.getEnv().setStoreTerminalId("some_setStoreTerminalId");
        order.getEnv().setStoreTerminalRequestTime("2020-06-11T13:35:02+08:00");

        String paymentRequestId = "PR20190000000001_";
        String buyerPaymentCode = "288888888888888888";

        return new UserPresentedCodePaymentRequest(cfg, paymentRequestId, order, currency,
            amountInCents, buyerPaymentCode);
    }
}
