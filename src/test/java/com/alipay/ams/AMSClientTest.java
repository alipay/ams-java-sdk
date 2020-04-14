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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import com.alipay.ams.domain.RequestHeader;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMSClientTest.java, v 0.1 2020年4月3日 下午2:40:14 guangling.zgl Exp $
 */
public class AMSClientTest extends BaseAMSTest {

    @Test
    public void testIOException() throws Exception {

        MockSetup mockSetup = super.mockSetupWithIOException();

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentId(mockSetup.getAMSSettings(), "xxx"),
            mockSetup.getPaymentInquiryCallback());

        //1. onIOException
        verify(mockSetup.getPaymentInquiryCallback(), times(1)).onIOException(
            any(IOException.class), eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class));

        HttpPost requestUsed = mockSetup.getHttpPostArgumentCaptor().getValue();
        //2. request headers
        TestUtil.assertRequestHeaders(requestUsed);

        //3. request body content match
        byte[] byteArray = IOUtils.toByteArray(requestUsed.getEntity().getContent());
        assertEquals("{\"paymentId\":\"xxx\"}", new String(byteArray));

        //4. reportRequestStart        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //5. no reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), never()).reportResponseReceived(
            any(PaymentInquiryRequest.class));
    }

    @Test
    public void testHttpStatusNot200() throws Exception {

        MockSetup mockSetup = super.mockSetupWithHttpStatusNot200();

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentId(mockSetup.getAMSSettings(), "xxx"),
            mockSetup.getPaymentInquiryCallback());

        //1. no onIOException
        verify(mockSetup.getPaymentInquiryCallback(), never()).onIOException(
            any(IOException.class), eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class));

        //2. onHttpStatusNot200
        verify(mockSetup.getPaymentInquiryCallback(), times(1)).onHttpStatusNot200(
            eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class), eq(300));

        HttpPost requestUsed = mockSetup.getHttpPostArgumentCaptor().getValue();
        TestUtil.assertRequestHeaders(requestUsed);

        //3. request body content match
        byte[] byteArray = IOUtils.toByteArray(requestUsed.getEntity().getContent());
        assertEquals("{\"paymentId\":\"xxx\"}", new String(byteArray));

        //4. reportRequestStart        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //5. reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //6.response closed
        verify(mockSetup.getCloseableHttpResponse(), times(1)).close();
    }

    @Test
    public void testResponseHeadersInvalid() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse(false, "/responses/upcp_f.json");

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentId(mockSetup.getAMSSettings(), "xxx"),
            mockSetup.getPaymentInquiryCallback());

        //1. no onIOException
        verify(mockSetup.getPaymentInquiryCallback(), never()).onIOException(
            any(IOException.class), eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class));

        //2. no onHttpStatusNot200
        verify(mockSetup.getPaymentInquiryCallback(), never()).onHttpStatusNot200(
            eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class), eq(300));

        HttpPost requestUsed = mockSetup.getHttpPostArgumentCaptor().getValue();
        TestUtil.assertRequestHeaders(requestUsed);

        //3. request body content match
        byte[] byteArray = IOUtils.toByteArray(requestUsed.getEntity().getContent());
        assertEquals("{\"paymentId\":\"xxx\"}", new String(byteArray));

        //4. reportRequestStart        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //5. reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //6.response closed
        verify(mockSetup.getCloseableHttpResponse(), times(1)).close();

        //7. onSignatureVerifyFailed
        verify(mockSetup.getPaymentInquiryCallback(), times(1)).onSignatureVerifyFailed(
            any(PaymentInquiryRequest.class), any(ResponseHeader.class), any(String.class));
    }

    @Test
    public void testSignatureVerifyFailed() throws Exception {

        MockSetup mockSetup = super.mockSetupWithResponse(false, "/responses/upcp_f.json");

        mockSetup.getAmsClient().execute(
            PaymentInquiryRequest.byPaymentId(mockSetup.getAMSSettings(), "xxx"),
            mockSetup.getPaymentInquiryCallback());

        Header[] responseHeaders = new Header[] {
                new BasicHeader("client-id", "some_client_id"),
                new BasicHeader(
                    "signature",
                    "algorithm=RSA256,keyVersion=1,signature=Z5jzph9LOJRhesviKFs74ogWHz2ufDcKYA4XQL8wz41bRnyg%2FbDsIOAGQ%2BGzSwhNTV1azpC02%2F4itzkjxDH%2BJXOzjNWVVIQvhzT7UTgbjRMuaBi3hs8SVSTJzqkSEAiLLkqgJ3te2slOWwlPLXaFJdXAEw7Gd2EhXJyPE9FAn5sKBZsLf0nPqVYNhIy3iINf9PAKNJ9qzJVEj8VEH34hovUqZtgIlD%2F7%2BBR9ieoe8kQjSxE6qenbLUFMravbAicu3KQbR05wBApFDXkC%2BTLS9mwyCsMlQrGXfYWwO%2FLluXRE3jkgalO5jdaEfzawzz%2BqkuU%2FJdM7iHXBMJGC%2FQdsXA%3D%3D") };

        doReturn(responseHeaders).when(mockSetup.getCloseableHttpResponse()).getAllHeaders();

        //1. no onIOException
        verify(mockSetup.getPaymentInquiryCallback(), never()).onIOException(
            any(IOException.class), eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class));

        //2. no onHttpStatusNot200
        verify(mockSetup.getPaymentInquiryCallback(), never()).onHttpStatusNot200(
            eq(mockSetup.getAmsClient()), any(PaymentInquiryRequest.class), eq(300));

        HttpPost requestUsed = mockSetup.getHttpPostArgumentCaptor().getValue();
        TestUtil.assertRequestHeaders(requestUsed);

        //3. request body content match
        byte[] byteArray = IOUtils.toByteArray(requestUsed.getEntity().getContent());
        assertEquals("{\"paymentId\":\"xxx\"}", new String(byteArray));

        //4. reportRequestStart        
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportRequestStart(
            any(PaymentInquiryRequest.class), any(RequestHeader.class));

        //5. reportResponseReceived
        verify(mockSetup.getTelemetrySupportPaymentInquiry(), times(1)).reportResponseReceived(
            any(PaymentInquiryRequest.class));

        //6.response closed
        verify(mockSetup.getCloseableHttpResponse(), times(1)).close();

        //7. onSignatureVerifyFailed
        verify(mockSetup.getPaymentInquiryCallback(), times(1)).onSignatureVerifyFailed(
            any(PaymentInquiryRequest.class), any(ResponseHeader.class), any(String.class));
    }

}
