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
package com.alipay.ams.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.ArgumentCaptor;

import com.alipay.ams.ApacheHttpPostAMSClient;
import com.alipay.ams.callbacks.JobSupport;
import com.alipay.ams.callbacks.PaymentCancelCallback;
import com.alipay.ams.callbacks.PaymentContextSupport;
import com.alipay.ams.callbacks.PaymentInquiryCallback;
import com.alipay.ams.callbacks.PaymentStatusUpdateCallback;
import com.alipay.ams.callbacks.TelemetrySupport;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.PaymentInquiryResponse;
import com.alipay.ams.domain.responses.UserPresentedCodePaymentResponse;
import com.alipay.ams.job.Job;
import com.alipay.ams.job.JobExecutor;

/**
 * 
 * @author guangling.zgl
 * @version $Id: MockSetup.java, v 0.1 2020年4月7日 下午6:49:40 guangling.zgl Exp $
 */
@SuppressWarnings("unchecked")
public abstract class MockSetup {

    //The mocks
    protected CloseableHttpClient                                                                 closeableHttpClient            = mock(CloseableHttpClient.class);
    protected TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse>                     telemetrySupportPaymentInquiry = mock(TelemetrySupport.class);
    protected CloseableHttpResponse                                                               closeableHttpResponse          = mock(CloseableHttpResponse.class);
    protected HttpEntity                                                                          entity                         = mock(HttpEntity.class);
    protected PaymentCancelCallback                                                               paymentCancelCallback          = mock(PaymentCancelCallback.class);
    protected PaymentContextSupport                                                               paymentContextSupport          = mock(PaymentContextSupport.class);
    protected PaymentContext                                                                      context                        = mock(PaymentContext.class);
    protected JobSupport                                                                          jobSupport                     = mock(JobSupport.class);
    protected PaymentStatusUpdateCallback                                                         paymentStatusUpdateCallback    = mock(PaymentStatusUpdateCallback.class);
    protected TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP    = mock(TelemetrySupport.class);
    protected PaymentInquiryCallback                                                              paymentInquiryCallback         = mock(PaymentInquiryCallback.class);

    //The ArgumentCaptors
    protected ArgumentCaptor<HttpPost>                                                            httpPostArgumentCaptor         = ArgumentCaptor
                                                                                                                                     .forClass(HttpPost.class);
    protected ArgumentCaptor<Job>                                                                 jobCaptor                      = ArgumentCaptor
                                                                                                                                     .forClass(Job.class);

    //The settings
    protected AMSSettings                                                                         cfg;

    //The client
    protected ApacheHttpPostAMSClient                                                             amsClient;

    /**
     * @throws IOException 
     * @throws ClientProtocolException 
     * 
     */
    public MockSetup(final boolean skipSignatureVerification) throws Exception {

        this.cfg = new AMSSettings() {

            @Override
            public boolean isDevMode() {
                return skipSignatureVerification;
            }

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

        this.amsClient = new ApacheHttpPostAMSClient(cfg);
        this.amsClient.setHttpClient(closeableHttpClient);
        JobExecutor.instance.setJobSupport(jobSupport);

        //common setups
        Header[] responseHeaders = new Header[0];
        when(closeableHttpResponse.getAllHeaders()).thenReturn(responseHeaders);

        when(context.getPaymentRequestId()).thenReturn("some_id");

        doNothing().when(jobSupport).insertNewJob(jobCaptor.capture());

        when(
            paymentContextSupport.loadContextByPaymentRequestIdOrDefault(any(String.class),
                any(PaymentContext.class))).thenReturn(context);

        when(paymentInquiryCallback.getPaymentCancelCallback()).thenReturn(paymentCancelCallback);
        when(paymentInquiryCallback.getTelemetrySupport()).thenReturn(
            telemetrySupportPaymentInquiry);

        when(paymentCancelCallback.getPaymentContextSupport()).thenReturn(paymentContextSupport);
        when(paymentCancelCallback.getPaymentStatusUpdateCallback()).thenReturn(
            paymentStatusUpdateCallback);

        //call subclass to setup mocks.
        setup();
    }

    /**
     * @throws Exception 
     * 
     */
    protected abstract void setup() throws Exception;

    /**
     * Getter method for property <tt>telemetrySupportPaymentUPCP</tt>.
     * 
     * @return property value of telemetrySupportPaymentUPCP
     */
    public TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> getTelemetrySupportPaymentUPCP() {
        return telemetrySupportPaymentUPCP;
    }

    /**
     * Setter method for property <tt>telemetrySupportPaymentUPCP</tt>.
     * 
     * @param telemetrySupportPaymentUPCP value to be assigned to property telemetrySupportPaymentUPCP
     */
    public void setTelemetrySupportPaymentUPCP(TelemetrySupport<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse> telemetrySupportPaymentUPCP) {
        this.telemetrySupportPaymentUPCP = telemetrySupportPaymentUPCP;
    }

    /**
     * Getter method for property <tt>context</tt>.
     * 
     * @return property value of context
     */
    public PaymentContext getContext() {
        return context;
    }

    /**
     * Setter method for property <tt>context</tt>.
     * 
     * @param context value to be assigned to property context
     */
    public void setContext(PaymentContext context) {
        this.context = context;
    }

    /**
     * Getter method for property <tt>paymentStatusUpdateCallback</tt>.
     * 
     * @return property value of paymentStatusUpdateCallback
     */
    public PaymentStatusUpdateCallback getPaymentStatusUpdateCallback() {
        return paymentStatusUpdateCallback;
    }

    /**
     * Setter method for property <tt>paymentStatusUpdateCallback</tt>.
     * 
     * @param paymentStatusUpdateCallback value to be assigned to property paymentStatusUpdateCallback
     */
    public void setPaymentStatusUpdateCallback(PaymentStatusUpdateCallback paymentStatusUpdateCallback) {
        this.paymentStatusUpdateCallback = paymentStatusUpdateCallback;
    }

    /**
     * Getter method for property <tt>aMSClient</tt>.
     * 
     * @return property value of aMSClient
     */
    public ApacheHttpPostAMSClient getAmsClient() {
        return amsClient;
    }

    /**
     * Setter method for property <tt>aMSClient</tt>.
     * 
     * @param aMSClient value to be assigned to property aMSClient
     */
    public void setAMSClient(ApacheHttpPostAMSClient aMSClient) {
        this.amsClient = aMSClient;
    }

    /**
     * Getter method for property <tt>telemetrySupportPaymentInquiry</tt>.
     * 
     * @return property value of telemetrySupportPaymentInquiry
     */
    public TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> getTelemetrySupportPaymentInquiry() {
        return telemetrySupportPaymentInquiry;
    }

    /**
     * Setter method for property <tt>telemetrySupportPaymentInquiry</tt>.
     * 
     * @param telemetrySupportPaymentInquiry value to be assigned to property telemetrySupportPaymentInquiry
     */
    public void setTelemetrySupportPaymentInquiry(TelemetrySupport<PaymentInquiryRequest, PaymentInquiryResponse> telemetrySupportPaymentInquiry) {
        this.telemetrySupportPaymentInquiry = telemetrySupportPaymentInquiry;
    }

    /**
     * Getter method for property <tt>httpPostArgumentCaptor</tt>.
     * 
     * @return property value of httpPostArgumentCaptor
     */
    public ArgumentCaptor<HttpPost> getHttpPostArgumentCaptor() {
        return httpPostArgumentCaptor;
    }

    /**
     * Setter method for property <tt>httpPostArgumentCaptor</tt>.
     * 
     * @param httpPostArgumentCaptor value to be assigned to property httpPostArgumentCaptor
     */
    public void setHttpPostArgumentCaptor(ArgumentCaptor<HttpPost> httpPostArgumentCaptor) {
        this.httpPostArgumentCaptor = httpPostArgumentCaptor;
    }

    /**
     * Getter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @return property value of paymentCancelCallback
     */
    public PaymentCancelCallback getPaymentCancelCallback() {
        return paymentCancelCallback;
    }

    /**
     * Setter method for property <tt>paymentCancelCallback</tt>.
     * 
     * @param paymentCancelCallback value to be assigned to property paymentCancelCallback
     */
    public void setPaymentCancelCallback(PaymentCancelCallback paymentCancelCallback) {
        this.paymentCancelCallback = paymentCancelCallback;
    }

    /**
     * Getter method for property <tt>aMSSettings</tt>.
     * 
     * @return property value of aMSSettings
     */
    public AMSSettings getAMSSettings() {
        return cfg;
    }

    /**
     * Setter method for property <tt>cfg</tt>.
     * 
     * @param AMSSettings value to be assigned to property cfg
     */
    public void setAMSSettings(AMSSettings cfg) {
        this.cfg = cfg;
    }

    /**
     * Getter method for property <tt>jobSupport</tt>.
     * 
     * @return property value of jobSupport
     */
    public JobSupport getJobSupport() {
        return jobSupport;
    }

    /**
     * Setter method for property <tt>jobSupport</tt>.
     * 
     * @param jobSupport value to be assigned to property jobSupport
     */
    public void setJobSupport(JobSupport jobSupport) {
        this.jobSupport = jobSupport;
    }

    /**
     * Getter method for property <tt>jobCaptor</tt>.
     * 
     * @return property value of jobCaptor
     */
    public ArgumentCaptor<Job> getJobCaptor() {
        return jobCaptor;
    }

    /**
     * Setter method for property <tt>jobCaptor</tt>.
     * 
     * @param jobCaptor value to be assigned to property jobCaptor
     */
    public void setJobCaptor(ArgumentCaptor<Job> jobCaptor) {
        this.jobCaptor = jobCaptor;
    }

    /**
     * Getter method for property <tt>paymentInquiryCallback</tt>.
     * 
     * @return property value of paymentInquiryCallback
     */
    public PaymentInquiryCallback getPaymentInquiryCallback() {
        return paymentInquiryCallback;
    }

    /**
     * Setter method for property <tt>paymentInquiryCallback</tt>.
     * 
     * @param paymentInquiryCallback value to be assigned to property paymentInquiryCallback
     */
    public void setPaymentInquiryCallback(PaymentInquiryCallback paymentInquiryCallback) {
        this.paymentInquiryCallback = paymentInquiryCallback;
    }

    /**
     * Getter method for property <tt>closeableHttpResponse</tt>.
     * 
     * @return property value of closeableHttpResponse
     */
    public CloseableHttpResponse getCloseableHttpResponse() {
        return closeableHttpResponse;
    }

    /**
     * Setter method for property <tt>closeableHttpResponse</tt>.
     * 
     * @param closeableHttpResponse value to be assigned to property closeableHttpResponse
     */
    public void setCloseableHttpResponse(CloseableHttpResponse closeableHttpResponse) {
        this.closeableHttpResponse = closeableHttpResponse;
    }

}
