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
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.job.Job;
import com.alipay.ams.job.Job.Type;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentCancelCallbackTest.java, v 0.1 2020年4月8日 下午3:31:24 guangling.zgl Exp $
 */
public class PaymentCancelCallbackTest extends BaseAMSTest {

    @Test
    public void onIOExceptionAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests
        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("cancel uri not expected",
            requestsUsed.get(0).getURI().toString().endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never()).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.CANCEL, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void onIOExceptionAndNoMoreRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        when(mockSetup.getContext().getCancelCount()).thenReturn(
            mockSetup.getAMSSettings().maxCancelCount);

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never()).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200        
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //11. Alarm triggered
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1))
            .reportPaymentCancelResultUnknown(any(String.class));
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).reportCancelResultUnknown(
            eq(mockSetup.getAmsClient()), eq(paymentCancelRequest));
    }

    @Test
    public void testOnHttpStatusNot200AndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Cancel
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.CANCEL, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void testOnUStatusAndRetry() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/upcp_u.json");

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Cancel
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. A retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.CANCEL, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void testOnFStatus() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_f.json");

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Cancel
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. Retry scheduled
        verify(mockSetup.getJobSupport(), times(1)).insertNewJob(any(Job.class));

        Job job = mockSetup.getJobCaptor().getValue();

        assertEquals(Type.CANCEL, job.getType());
        assertEquals("some_id", job.getPaymentRequestId());

    }

    @Test
    public void testOnSStatusSUCCESS() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        PaymentCancelCallback paymentCancelCallback = new PaymentCancelCallback(
            mockSetup.getPaymentContextSupport(), mockSetup.getPaymentStatusUpdateCallback());

        paymentCancelCallback.setTelemetrySupport(mockSetup.getTelemetrySupportPaymentCancel());

        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(
            mockSetup.getAMSSettings(), "some_id");

        mockSetup.getAmsClient().execute(paymentCancelRequest, paymentCancelCallback);
        mockSetup.getAmsClient().close();

        //1. one requests

        List<HttpPost> requestsUsed = mockSetup.getHttpPostArgumentCaptor().getAllValues();
        assertEquals(1, requestsUsed.size());

        //2. request headers and URI
        TestUtil.assertRequestHeaders(requestsUsed.get(0));

        assertTrue("inquiryPayment uri not expected", requestsUsed.get(0).getURI().toString()
            .endsWith("/ams/api/v1/payments/cancel"));

        //3. request body content match
        assertEquals("{\"paymentRequestId\":\"some_id\"}",
            new String(IOUtils.toByteArray(requestsUsed.get(0).getEntity().getContent())));

        //7. reportRequestStart of cancel      
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportRequestStart(
            any(PaymentCancelRequest.class), any(RequestHeader.class));

        //8. One reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportResponseReceived(
            any(PaymentCancelRequest.class));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Cancel
        verify(mockSetup.getTelemetrySupportPaymentCancel(), never())
            .reportRequestIOExceptionOrHttpStatusNot200(any(PaymentCancelRequest.class));

        //10. No retry scheduled
        verify(mockSetup.getJobSupport(), never()).insertNewJob(any(Job.class));

        //12 reportPaymentS
        verify(mockSetup.getTelemetrySupportPaymentCancel(), times(1)).reportPaymentCanceled(
            eq("some_id"));

        //13 handlePaymentSuccess
        verify(mockSetup.getPaymentStatusUpdateCallback(), times(1)).onPaymentCancelled(
            any(String.class), any(String.class), any(String.class));
    }

}
