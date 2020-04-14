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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Currency;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Env;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.requests.EntryCodePaymentRequest;
import com.alipay.ams.domain.responses.EntryCodePaymentResponse;
import com.alipay.ams.job.Job;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: EntryCodePaymentCallbackTest.java, v 0.1 2020年4月8日 下午3:31:24 guangling.zgl Exp $
 */
public class EntryCodePaymentCallbackTest extends BaseAMSTest {

    @Test
    public void onIOExceptionAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();
        when(mockSetup.getContext().getEntryCodeRequestCount()).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount - 1).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount);

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. Two requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(2, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));
        TestUtil.assertRequestHeaders(requestsUsed.get(1));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), never()).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9. Two reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2))
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

    }

    @Test
    public void onIOExceptionAndNoMoreRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();
        when(mockSetup.getContext().getEntryCodeRequestCount()).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount);

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), never()).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9.  reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1))
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. whoIsCalled
        verify(entryCodePaymentCallback).onGetEntryCodeFailed(any(EntryCodePaymentRequest.class),
            any(ResponseResult.class));
        verify(entryCodePaymentCallback, never()).onEntryCodeResponse(
            any(EntryCodePaymentResponse.class));

    }

    @Test
    public void testOnHttpStatusNot200AndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();
        when(mockSetup.getContext().getEntryCodeRequestCount()).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount - 1).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount);

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(2, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));
        TestUtil.assertRequestHeaders(requestsUsed.get(1));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));
        assertTrue("uri not expected",
            requestsUsed.get(1).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(1).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8.  reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2)).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9.  reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2))
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. whoIsCalled
        verify(entryCodePaymentCallback).onGetEntryCodeFailed(any(EntryCodePaymentRequest.class),
            any(ResponseResult.class));
        verify(entryCodePaymentCallback, never()).onEntryCodeResponse(
            any(EntryCodePaymentResponse.class));

    }

    @Test
    public void testOnUStatusAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_u.json",
            "/responses/upcp_u.json");

        when(mockSetup.getContext().getEntryCodeRequestCount()).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount - 1).thenReturn(
            mockSetup.getAMSSettings().maxEntryCodeRequestCount);

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. Two requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(2, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));
        TestUtil.assertRequestHeaders(requestsUsed.get(1));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));
        assertTrue("uri not expected",
            requestsUsed.get(1).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(1).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8.  reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(2)).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9.  reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. whoIsCalled
        verify(entryCodePaymentCallback).onGetEntryCodeFailed(any(EntryCodePaymentRequest.class),
            any(ResponseResult.class));
        verify(entryCodePaymentCallback, never()).onEntryCodeResponse(
            any(EntryCodePaymentResponse.class));

    }

    @Test
    public void testOnFStatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_f.json");

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. one requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8. reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1)).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9.  No reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(0))
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. whoIsCalled
        verify(entryCodePaymentCallback).onGetEntryCodeFailed(any(EntryCodePaymentRequest.class),
            any(ResponseResult.class));
        verify(entryCodePaymentCallback, never()).onEntryCodeResponse(
            any(EntryCodePaymentResponse.class));

    }

    @Test
    public void testOnSStatusSUCCESS() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/entry_code_s.json");

        EntryCodePaymentCallback entryCodePaymentCallback = spy(new EntryCodePaymentCallback(
            mockSetup.getPaymentContextSupport()) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
            }
        });

        entryCodePaymentCallback.setTelemetrySupport(mockSetup
            .getTelemetrySupportPaymentEntryCode());

        mockSetup.getAmsClient().execute(newRequest(mockSetup.getAMSSettings()),
            entryCodePaymentCallback);
        mockSetup.getAmsClient().close();

        //1. one requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/pay"));

        //3. request body content match
        assertEquals(new String(TestUtil.getApiMocks("/requests/entry_code.json"), "utf-8"),
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent()), "utf-8"));

        //7. reportRequestStart      
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1)).reportRequestStart(
            any(EntryCodePaymentRequest.class), any(RequestHeader.class));

        //8. reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(1)).reportResponseReceived(
            any(EntryCodePaymentRequest.class));

        //9.  No reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentEntryCode(), times(0))
            .reportRequestIOExceptionOrHttpStatusNot200(any(EntryCodePaymentRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. whoIsCalled
        verify(entryCodePaymentCallback).onEntryCodeResponse(any(EntryCodePaymentResponse.class));
        verify(entryCodePaymentCallback, never()).onGetEntryCodeFailed(
            any(EntryCodePaymentRequest.class), any(ResponseResult.class));
    }

    /**
     * 
     * @param cfg
     * @return
     */
    private EntryCodePaymentRequest newRequest(AMSSettings cfg) {

        long amountInCents = 1000l;
        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, amountInCents));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));
        order
            .setEnv(new Env(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15G77 NebulaSDK/1.8.100112 Nebula PSDType(1) AlipayDefined(nt:4G,ws:320|504|2.0) AliApp(AP/10.1.32.600) AlipayClient/10.1.32.600 Alipay Language/zh-Hans AlipayConnect"));

        String paymentRequestId = "PR20190000000001_";

        return new EntryCodePaymentRequest(cfg, paymentRequestId, order, currency, amountInCents);
    }
}
