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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alipay.ams.AMSClient;
import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.job.Job;
import com.alipay.ams.job.JobExecutor;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: PaymentStatusUpdateCallbackTest.java, v 0.1 2020年4月9日 下午2:49:22 guangling.zgl Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentStatusUpdateCallbackTest extends BaseAMSTest {

    @Mock
    private LockSupport lockSupport;

    @Test
    public void testOnSStatusSUCCESS() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        when(lockSupport.tryLock4PaymentStatusUpdate(anyString(), anyLong(), any(TimeUnit.class)))
            .thenReturn(true);

        PaymentStatusUpdateCallback paymentStatusUpdateCallback = spy(new PaymentStatusUpdateCallback(
            mockSetup.getAMSSettings(), lockSupport) {

            @Override
            public void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request) {
            }

            @Override
            protected void onPaymentSuccess(PaymentResultModel paymentResultModel) {
            }

            @Override
            public void onPaymentFailed(String paymentRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onPaymentCancelled(String paymentRequestId, String paymentId,
                                           String cancelTime) {
            }

            @Override
            public boolean isPaymentStatusSuccess(String paymentRequestId) {
                return false;
            }

            @Override
            public boolean isPaymentStatusCancelled(String paymentRequestId) {
                return false;
            }
        });
        when(mockSetup.getPaymentCancelCallback().getPaymentStatusUpdateCallback()).thenReturn(
            paymentStatusUpdateCallback);

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
        verify(paymentStatusUpdateCallback, times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));
        verify(paymentStatusUpdateCallback, times(1)).onPaymentSuccess(
            any(PaymentResultModel.class));
    }

    @Test
    public void testOnSStatusAlreadySUCCESS() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        when(lockSupport.tryLock4PaymentStatusUpdate(anyString(), anyLong(), any(TimeUnit.class)))
            .thenReturn(true);

        PaymentStatusUpdateCallback paymentStatusUpdateCallback = spy(new PaymentStatusUpdateCallback(
            mockSetup.getAMSSettings(), lockSupport) {

            @Override
            public void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request) {
            }

            @Override
            protected void onPaymentSuccess(PaymentResultModel paymentResultModel) {
            }

            @Override
            public void onPaymentFailed(String paymentRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onPaymentCancelled(String paymentRequestId, String paymentId,
                                           String cancelTime) {
            }

            @Override
            public boolean isPaymentStatusSuccess(String paymentRequestId) {
                return true;
            }

            @Override
            public boolean isPaymentStatusCancelled(String paymentRequestId) {
                return false;
            }
        });
        when(mockSetup.getPaymentCancelCallback().getPaymentStatusUpdateCallback()).thenReturn(
            paymentStatusUpdateCallback);

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
        verify(paymentStatusUpdateCallback, times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));
        verify(paymentStatusUpdateCallback, times(0)).onPaymentSuccess(
            any(PaymentResultModel.class));
    }

    @Test
    public void testOnSStatusAlreadyCanceled() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        when(lockSupport.tryLock4PaymentStatusUpdate(anyString(), anyLong(), any(TimeUnit.class)))
            .thenReturn(true);

        PaymentStatusUpdateCallback paymentStatusUpdateCallback = spy(new PaymentStatusUpdateCallback(
            mockSetup.getAMSSettings(), lockSupport) {

            @Override
            public void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request) {
            }

            @Override
            protected void onPaymentSuccess(PaymentResultModel paymentResultModel) {
            }

            @Override
            public void onPaymentFailed(String paymentRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onPaymentCancelled(String paymentRequestId, String paymentId,
                                           String cancelTime) {
            }

            @Override
            public boolean isPaymentStatusSuccess(String paymentRequestId) {
                return false;
            }

            @Override
            public boolean isPaymentStatusCancelled(String paymentRequestId) {
                return true;
            }
        });
        when(mockSetup.getPaymentCancelCallback().getPaymentStatusUpdateCallback()).thenReturn(
            paymentStatusUpdateCallback);

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
        verify(paymentStatusUpdateCallback, times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));
        verify(paymentStatusUpdateCallback, times(0)).onPaymentSuccess(
            any(PaymentResultModel.class));
    }

    @Test
    public void testOnSStatusLockFailed() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");

        when(lockSupport.tryLock4PaymentStatusUpdate(anyString(), anyLong(), any(TimeUnit.class)))
            .thenReturn(false);

        ScheduledThreadPoolExecutor executor = mock(ScheduledThreadPoolExecutor.class);
        JobExecutor.instance.setExecutor(executor);

        PaymentStatusUpdateCallback paymentStatusUpdateCallback = spy(new PaymentStatusUpdateCallback(
            mockSetup.getAMSSettings(), lockSupport) {

            @Override
            public void reportCancelResultUnknown(AMSClient client, PaymentCancelRequest request) {
            }

            @Override
            protected void onPaymentSuccess(PaymentResultModel paymentResultModel) {
            }

            @Override
            public void onPaymentFailed(String paymentRequestId, ResponseResult responseResult) {
            }

            @Override
            public void onPaymentCancelled(String paymentRequestId, String paymentId,
                                           String cancelTime) {
            }

            @Override
            public boolean isPaymentStatusSuccess(String paymentRequestId) {
                return false;
            }

            @Override
            public boolean isPaymentStatusCancelled(String paymentRequestId) {
                return true;
            }
        });
        when(mockSetup.getPaymentCancelCallback().getPaymentStatusUpdateCallback()).thenReturn(
            paymentStatusUpdateCallback);

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
        verify(paymentStatusUpdateCallback, times(1)).handlePaymentSuccess(
            any(PaymentResultModel.class));
        verify(paymentStatusUpdateCallback, times(0)).onPaymentSuccess(
            any(PaymentResultModel.class));
        verify(executor).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
    }
}
