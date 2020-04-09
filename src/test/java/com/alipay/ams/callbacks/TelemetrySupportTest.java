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

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alipay.ams.BaseAMSTest;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResultStatusType;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.domain.telemetry.Call;
import com.alipay.ams.domain.telemetry.Telemetry;
import com.alipay.ams.util.MockSetup;

/**
 * 
 * @author guangling.zgl
 * @version $Id: TelemetrySupportTest.java, v 0.1 2020年4月9日 下午6:37:38 guangling.zgl Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class TelemetrySupportTest extends BaseAMSTest {
    @Mock
    private PaymentContextSupport paymentContextSupport;
    @Mock
    private PaymentContext        paymentContext;
    @Mock
    private Call                  call;
    @Mock
    private Telemetry             telemetry;

    @Test
    public void testOnSStatusSUCCESS() throws Exception {

        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupport = spy(new TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse>(
            paymentContextSupport) {

            @Override
            protected Call getCurrentCall(PaymentContext paymentContext) {
                return call;
            }

            @Override
            protected String getPaymentRequestId(PaymentInquiryRequest request) {
                return null;
            }

            @Override
            protected String getAgentToken(PaymentInquiryRequest request) {
                return null;
            }
        });

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");
        PaymentContextSupport.prevRequestTelemetry.add("some_id");
        ArgumentCaptor<RequestHeader> headerArgumentCaptor = ArgumentCaptor
            .forClass(RequestHeader.class);

        when(
            paymentContextSupport.loadContextByPaymentRequestIdOrDefault(anyString(),
                any(PaymentContext.class))).thenReturn(paymentContext);
        when(paymentContext.getTelemetry()).thenReturn(telemetry);

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(telemetrySupport);

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        InOrder inOrder = inOrder(telemetrySupport);

        //7. reportRequestStart of inquiry      
        inOrder.verify(telemetrySupport, times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), headerArgumentCaptor.capture());
        RequestHeader requestHeader = headerArgumentCaptor.getValue();
        assertFalse(requestHeader.extraHeaders().containsKey("X-Telemetry"));

        //8. One reportResponseReceived
        inOrder.verify(telemetrySupport, times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //12 reportResultStatus
        inOrder.verify(telemetrySupport, times(1)).reportResultStatus(
            any(PaymentInquiryRequest.class), any(ResultStatusType.class));

        //12 reportPaymentS
        inOrder.verify(telemetrySupport, times(1)).reportPaymentS(eq("some_id"));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(telemetrySupport, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

        verify(call).setResultStatusType(any(ResultStatusType.class));
        verify(call).setResponseReceivedMs(anyLong());
        verify(call).setRequestStartMs(anyLong());
        verify(telemetry).setPaymentSuccessed();
        verify(paymentContextSupport, times(4)).saveContext(any(PaymentContext.class));

    }

    @Test
    public void testNotReady() throws Exception {

        TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupport = spy(new TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse>(
            null) {

            @Override
            protected Call getCurrentCall(PaymentContext paymentContext) {
                return call;
            }

            @Override
            protected String getPaymentRequestId(PaymentInquiryRequest request) {
                return null;
            }

            @Override
            protected String getAgentToken(PaymentInquiryRequest request) {
                return null;
            }
        });

        MockSetup mockSetup = super.mockSetupWithResponse("/responses/inquiry_ss.json");
        PaymentContextSupport.prevRequestTelemetry.add("some_id");
        ArgumentCaptor<RequestHeader> headerArgumentCaptor = ArgumentCaptor
            .forClass(RequestHeader.class);

        PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
            mockSetup.getPaymentCancelCallback());
        paymentInquiryCallback.setTelemetrySupport(telemetrySupport);

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentRequestId(mockSetup.getAMSSettings(), "some_id"),
            paymentInquiryCallback);
        mockSetup.getAmsClient().close();

        InOrder inOrder = inOrder(telemetrySupport);

        //7. reportRequestStart of inquiry      
        inOrder.verify(telemetrySupport, times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), headerArgumentCaptor.capture());
        RequestHeader requestHeader = headerArgumentCaptor.getValue();
        assertFalse(requestHeader.extraHeaders().containsKey("X-Telemetry"));

        //8. One reportResponseReceived
        inOrder.verify(telemetrySupport, times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //12 reportResultStatus
        inOrder.verify(telemetrySupport, times(1)).reportResultStatus(
            any(PaymentInquiryRequest.class), any(ResultStatusType.class));

        //12 reportPaymentS
        inOrder.verify(telemetrySupport, times(1)).reportPaymentS(eq("some_id"));

        //9. no reportRequestIOExceptionOrHttpStatusNot200 for Inquiry
        verify(telemetrySupport, never()).reportRequestIOExceptionOrHttpStatusNot200(
            any(PaymentInquiryRequest.class));

        verify(call, never()).setResultStatusType(any(ResultStatusType.class));
        verify(call, never()).setResponseReceivedMs(anyLong());
        verify(call, never()).setRequestStartMs(anyLong());
        verify(telemetry, never()).setPaymentSuccessed();
        verify(paymentContextSupport, never()).saveContext(any(PaymentContext.class));

    }
}
