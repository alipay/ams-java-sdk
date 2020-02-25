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
package amsSdkDemo;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

import com.alipay.ams.AMS;
import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.EntryCodePaymentCallback;
import com.alipay.ams.callbacks.LockSupport;
import com.alipay.ams.callbacks.OrderCodePaymentCallback;
import com.alipay.ams.callbacks.PaymentCancelCallback;
import com.alipay.ams.callbacks.PaymentInquiryCallback;
import com.alipay.ams.callbacks.PaymentRefundCallback;
import com.alipay.ams.callbacks.UserPresentedCodePaymentCallback;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.AuthNotifyModel;
import com.alipay.ams.domain.Env;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.NotifyCallback;
import com.alipay.ams.domain.NotifyRequestHeader;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.requests.EntryCodePaymentRequest;
import com.alipay.ams.domain.requests.OrderCodePaymentRequest;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.PaymentRefundRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.EntryCodePaymentResponse;
import com.alipay.ams.domain.responses.OrderCodePaymentResponse;
import com.alipay.ams.domain.responses.PaymentRefundResponse;
import com.alipay.ams.job.JobExecutor;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Demo.java, v 0.1 2019年8月26日 下午6:13:50 guangling.zgl Exp $
 */
public class Demo {

    AMSSettings                    cfg                    = new AMSSettings();

    LockSupport                    lockSupport            = new InMemoryCallbackImpl(cfg);

    InMemoryCallbackImpl           allInOne               = new InMemoryCallbackImpl(cfg,
                                                              lockSupport);

    private PaymentCancelCallback  paymentCancelCallback  = new PaymentCancelCallback(allInOne,
                                                              allInOne);

    private PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
                                                              paymentCancelCallback);

    {
        JobExecutor.instance.setClient(AMS.with(cfg));
        JobExecutor.instance.setJobSupport(allInOne);
        JobExecutor.instance.setPaymentInquiryCallback(paymentInquiryCallback);

        JobExecutor.instance.startJobExecutor();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Demo demo = new Demo();
        //        demo.orderCode();
        demo.entryCode();
        //        demo.pay();

        //        demo.inquiry();
        //
        //        demo.refund();
        //
        //        demo.onNotify();
        //
        //        demo.cancel();

    }

    void pay() {
        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, 1000l));//10.00USD
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        String buyerPaymentCode = "288888888888888888";
        long amountInCents = 1000l;

        final UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode);

        AMS.with(cfg)
            .execute(request, new UserPresentedCodePaymentCallback(paymentInquiryCallback));
    }

    /**
     * 
     */
    void onNotify() {
        String requestURI = null;
        Map<String, String> notifyRequestheaders = null;
        byte[] bodyContent = null;

        AMS.with(cfg).onNotify(requestURI, notifyRequestheaders, bodyContent,
            new NotifyCallback(allInOne) {

                @Override
                protected void onPaymentSuccess(AMSSettings settings,
                                                NotifyRequestHeader notifyRequestHeader,
                                                PaymentResultModel paymentResultModel) {
                    String paymentId = paymentResultModel.getPaymentId();

                }

                @Override
                protected void onAuthNotify(AMSSettings settings,
                                            NotifyRequestHeader notifyRequestHeader,
                                            AuthNotifyModel authNotifyModel) {
                }
            });
    }

    /**
     * 
     */
    void refund() {
        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(cfg,
            "201911041940108001001882C0203027168", "201911041940108001001882C0203027168_refund1",
            new Amount(Currency.getInstance("JPY"), 111l));
        AMS.with(cfg).execute(paymentRefundRequest, new PaymentRefundCallback() {

            @Override
            public void onUstatus(AMSClient client, PaymentRefundRequest paymentRefundRequest,
                                  ResponseResult responseResult) {
                cfg.logger.info("onUstatus: %s", responseResult);
            }

            @Override
            protected void onRefundSuccess(PaymentRefundResponse refundResponse) {
                cfg.logger.info("onRefundSuccess %s", refundResponse);
            }

            @Override
            protected void onRefundFailure(String refundRequestId, ResponseResult responseResult) {
                cfg.logger.info("onRefundFailure: %s", responseResult);
            }

            @Override
            public void onIOException(IOException e, AMSClient client, PaymentRefundRequest request) {
                cfg.logger.info("onIOException");
            }

            @Override
            public void onHttpStatusNot200(AMSClient client, PaymentRefundRequest request, int code) {
                cfg.logger.info("onHttpStatusNot200");
            }
        });
    }

    /**
     * 
     */
    void inquiry() {
        AMS.with(cfg).execute(
            PaymentInquiryRequest.byPaymentRequestId(cfg, "PR20190000000001_1571936707820"),
            paymentInquiryCallback);
    }

    void cancel() {
        AMS.with(cfg).execute(new PaymentCancelRequest(cfg, "PR20190000000001_1572943243242"),
            paymentCancelCallback);
    }

    void orderCode() {
        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, 1000l));//10.00USD
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        long amountInCents = 1000l;

        AMS.with(cfg).execute(
            new OrderCodePaymentRequest(cfg, paymentRequestId, order, currency, amountInCents),
            new OrderCodePaymentCallback(allInOne) {

                @Override
                protected void onOrderCodeResponse(OrderCodePaymentResponse orderCodeResponse) {
                    cfg.logger.info("onOrderCodeResponse: %s", orderCodeResponse);
                }

                @Override
                protected void onGetOrderCodeFailed(OrderCodePaymentRequest request,
                                                    ResponseResult responseResult) {
                    cfg.logger.info("onGetOrderCodeFailed: %s", responseResult);
                }
            });
    }

    void entryCode() {
        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, 1000l));//10.00USD
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));
        order
            .setEnv(new Env(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15G77 NebulaSDK/1.8.100112 Nebula PSDType(1) AlipayDefined(nt:4G,ws:320|504|2.0) AliApp(AP/10.1.32.600) AlipayClient/10.1.32.600 Alipay Language/zh-Hans AlipayConnect"));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        long amountInCents = 1000l;

        EntryCodePaymentRequest request = new EntryCodePaymentRequest(cfg, paymentRequestId, order,
            currency, amountInCents);
        request.setPaymentRedirectUrl("https://global.alipay.com/some_redirect");
        request.setPaymentNotifyUrl("https://global.alipay.com/some_notify");
        request.setPaymentExpiryTime(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
        AMS.with(cfg).execute(request, new EntryCodePaymentCallback(allInOne) {

            @Override
            protected void onGetEntryCodeFailed(EntryCodePaymentRequest request,
                                                ResponseResult responseResult) {
                cfg.logger.info("onGetEntryCodeFailed: %s", responseResult);

            }

            @Override
            protected void onEntryCodeResponse(EntryCodePaymentResponse entryCodeResponse) {
                cfg.logger.info("entryCodeResponse: %s", entryCodeResponse);
            }
        });
    }
}
