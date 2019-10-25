/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package amsSdkDemo;

import java.util.Currency;
import java.util.Map;

import com.alipay.ams.AMS;
import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.AuthNotifyModel;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.NotifyCallback;
import com.alipay.ams.domain.NotifyRequestHeader;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.PaymentContext;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.callbacks.InMemoryPaymentContextCallback;
import com.alipay.ams.domain.callbacks.PaymentCancelCallback;
import com.alipay.ams.domain.callbacks.PaymentContextCallback;
import com.alipay.ams.domain.callbacks.PaymentInquiryCallback;
import com.alipay.ams.domain.callbacks.PaymentRefundCallback;
import com.alipay.ams.domain.callbacks.UserPresentedCodePaymentCallback;
import com.alipay.ams.domain.reponses.PaymentCancelResponse;
import com.alipay.ams.domain.reponses.PaymentInquiryResponse;
import com.alipay.ams.domain.reponses.PaymentRefundResponse;
import com.alipay.ams.domain.reponses.UserPresentedCodePaymentResponse;
import com.alipay.ams.domain.requests.PaymentCancelRequest;
import com.alipay.ams.domain.requests.PaymentInquiryRequest;
import com.alipay.ams.domain.requests.PaymentRefundRequest;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Demo.java, v 0.1 2019年8月26日 下午6:13:50 guangling.zgl Exp $
 */
public class Demo {

    AMSSettings                    cfg                    = new AMSSettings();

    private PaymentContextCallback paymentContextCallback = new InMemoryPaymentContextCallback();

    private PaymentCancelCallback  paymentCancelCallback  = new PaymentCancelCallback(
                                                              paymentContextCallback) {

                                                              @Override
                                                              protected void scheduleALaterCancel(PaymentContext context,
                                                                                                  AMSSettings settings) {
                                                              }

                                                              @Override
                                                              protected void reportCancelResultUnknown(AMSClient client,
                                                                                                       PaymentCancelRequest request) {
                                                              }

                                                              @Override
                                                              protected void onCancelSuccess(PaymentCancelResponse cancelResponse) {
                                                              }

                                                              @Override
                                                              protected void onCancelFailure(ResponseResult responseResult) {
                                                              }
                                                          };

    private PaymentInquiryCallback paymentInquiryCallback = new PaymentInquiryCallback(
                                                              paymentCancelCallback,
                                                              paymentContextCallback) {

                                                              @Override
                                                              public void scheduleALaterInquiry(PaymentContext context,
                                                                                                AMSSettings amsSettings) {
                                                              }

                                                              @Override
                                                              public void onPaymentSuccess(PaymentInquiryResponse inquiryResponse) {
                                                              }

                                                              @Override
                                                              public void onPaymentFailure(PaymentInquiryResponse inquiryResponse) {
                                                              }
                                                          };

    /**
     * @param args
     */
    public static void main(String[] args) {
        Demo demo = new Demo();

        //        demo.pay();

        //        demo.inquiry();
        //
        //        demo.refund();
        //
        //        demo.onNotify();
        //
        demo.cancel();

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
        String buyerPaymentCode = "288888888812345678";
        long amountInCents = 1000l;

        final UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode, "some_token");

        AMS.with(cfg).execute(request,
            new UserPresentedCodePaymentCallback(paymentInquiryCallback) {

                @Override
                public void onDirectSuccess(UserPresentedCodePaymentResponse paymentResponse) {
                    System.out.println(paymentResponse.toString());
                }

                @Override
                public void onFailure(ResponseResult responseResult) {
                    System.out.println(responseResult.toString());
                }
            });
    }

    /**
     * 
     */
    void onNotify() {
        String requestURI = null;
        Map<String, String> notifyRequestheaders = null;
        byte[] bodyContent = null;

        AMS.with(cfg).onNotify(requestURI, notifyRequestheaders, bodyContent, new NotifyCallback() {

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
        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(cfg, "some_id",
            "some_id", new Amount(Currency.getInstance("JPY"), 111l), "some_time");
        AMS.with(cfg).execute(paymentRefundRequest, new PaymentRefundCallback() {

            @Override
            protected void onRefundSuccess(PaymentRefundResponse cancelResponse) {
            }

            @Override
            protected void onRefundFailure(ResponseResult responseResult) {
            }

            @Override
            protected void onRefundCallFailure() {
            }
        });
    }

    /**
     * 
     */
    void inquiry() {
        AMS.with(cfg).execute(
            PaymentInquiryRequest.byPaymentRequestId(cfg, "PR20190000000001_1571936707820"),
            new PaymentInquiryCallback(paymentCancelCallback, paymentContextCallback) {

                @Override
                public void scheduleALaterInquiry(PaymentContext context, AMSSettings amsSettings) {
                }

                @Override
                public void onPaymentSuccess(PaymentInquiryResponse inquiryResponse) {
                }

                @Override
                public void onPaymentFailure(PaymentInquiryResponse inquiryResponse) {
                }
            });
    }

    void cancel() {
        AMS.with(cfg).execute(
            PaymentCancelRequest.byPaymentId(cfg, "PR20190000000001_1571936707820"),
            new PaymentCancelCallback(paymentContextCallback) {

                @Override
                protected void scheduleALaterCancel(PaymentContext context, AMSSettings settings) {
                }

                @Override
                protected void reportCancelResultUnknown(AMSClient client,
                                                         PaymentCancelRequest request) {
                }

                @Override
                protected void onCancelSuccess(PaymentCancelResponse cancelResponse) {
                }

                @Override
                protected void onCancelFailure(ResponseResult responseResult) {
                }
            });
    }
}
