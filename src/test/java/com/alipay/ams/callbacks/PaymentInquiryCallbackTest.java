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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.job.Job;
import com.alipay.ams.job.Job.Type;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentInquiryCallbackTest.java, v 0.1 2020年4月7日 下午5:06:04 guangling.zgl Exp $
 */
public class PaymentInquiryCallbackTest extends BaseAMSTest {

    @Test
    public void onIOExceptionAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.INQUIRY, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void onIOExceptionAndNoMoreRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        when(mockSetup.getContext().getInquiryCount()).thenReturn(
            mockSetup.getAMSSettings().maxInquiryCount);

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. A cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), times(1)).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

    }

    @Test
    public void testOnHttpStatusNot200AndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.INQUIRY, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void testOnUStatusAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_u.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.INQUIRY, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void testOnFStatusWithORDER_NOT_EXIST() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_f.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.INQUIRY, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

        //11. No cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), never()).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

    }

    @Test
    public void testOnFStatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_f.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. A cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), times(1)).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

    }

    @Test
    public void testOnSStatusSUCCESS() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. No cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), never()).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

        //12 reportPaymentS
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportPaymentS(
            eq("some_id"));

        //13 handlePaymentSuccess
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));
    }

    @Test
    public void testOnSStatusFAIL() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_sf.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. No cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), never()).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

        //12 No reportPaymentS
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportPaymentS(eq("some_id"));

        //13 No handlePaymentSuccess
        verify(mockSetup.getPaymentStatusUpdateCallback(), never()).handlePaymentSuccess(
            any(PaymentResultModel.class));

        //12 reportPaymentS
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportPaymentF(
            eq("some_id"));

        //13 onPaymentFailed
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).onPaymentFailed(
            any(String.class), any(ResponseResult.class));
    }

    @Test
    public void testOnSStatusCANCELLED() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_sc.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. No cancel initiated.
        verify(mockSetup.getPaymentCancelCallback(), never()).scheduleALaterCancel(
            eq(mockSetup.getAmsClient().getSettings()), any(PaymentContext.class));

        //12 No reportPaymentS
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportPaymentS(eq("some_id"));

        //13 No handlePaymentSuccess
        verify(mockSetup.getPaymentStatusUpdateCallback(), never()).handlePaymentSuccess(
            any(PaymentResultModel.class));

        //12 reportPaymentCanceled
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportPaymentCanceled(
            any(String.class));

        //13 onPaymentCancelled
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).onPaymentCancelled(
            any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void testOnSStatusPROCESSING() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_sp.json");

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentInquiry());

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        //1. one requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/inquiryPayment"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of inquiry      
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentInquiryRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.INQUIRY, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());
    }
}
