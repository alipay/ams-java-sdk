# Alipay AMS Java Bindings 

[![Build Status](https://travis-ci.com/alipay/ams-java-sdk.svg?branch=master)](https://travis-ci.com/alipay/ams-java-sdk)
[![codecov](https://codecov.io/gh/alipay/ams-java-sdk/branch/master/graph/badge.svg)](https://codecov.io/gh/alipay/ams-java-sdk)
![license](https://img.shields.io/badge/license-MIT-green)

----

**Table of Contents**

  * [Requirements](#requirements)
  * [Installation](#installation)
     * [Maven users](#maven-users)
     * [Gradle users](#gradle-users)
  * [Documentation](#documentation)
  * [Usage](#usage)
     * [Preparing config.properties](#preparing-configproperties)
     * [1. To make an user-presented mode In-store payment:](#1-to-make-an-user-presented-mode-in-store-payment)
     * [2. To make an order code mode In-store payment:](#2-to-make-an-order-code-mode-in-store-payment)
     * [3. To make an entry code mode In-store payment:](#3-to-make-an-entry-code-mode-in-store-payment)
     * [4. To make a transaction inquiry:](#4-to-make-a-transaction-inquiry)
     * [5. To make a refund:](#5-to-make-a-refund)
     * [6. To make a cancellation:](#6-to-make-a-cancellation)
     * [7. To process a notify SPI request:](#7-to-process-a-notify-spi-request)
  * [Advanced Topic](#advanced-topic)
     * [Integration Best Practice](#integration-best-practice)
     * [Using API Mock](#using-api-mock)
     * [Acceptance testing](#acceptance-testing)
     * [Use your customized HTTP client instead of com.alipay.ams.ApacheHttpPostAMSClient](#use-your-customized-http-client-instead-of-comalipayamsapachehttppostamsclient)
  * [To get help](#to-get-help)
  * [FAQ](#faq)
     * [What if I only need to use the digital signature feature ?](#what-if-i-only-need-to-use-the-digital-signature-feature-)
  * [Change history](#change-history)

----
		
## Requirements

Java 1.6 or later.

## Installation

### Maven users

Add this repository to your project's POM:

```xml
<repositories>
    <repository>
        <id>ams-java-sdk-mvn-repo</id>
        <url>https://raw.github.com/alipay/ams-java-sdk/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

and dependency:

```xml
<dependency>
  <groupId>com.alipay.ams</groupId>
  <artifactId>ams-java</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.alipay.ams:ams-java:2.0.0"
```

## Documentation

Please see the [API docs](https://global.alipay.com/doc/ams) for the most up-to-date documentation.

## Usage

### Preparing config.properties

Put a file `config.properties` in your `src/main/resources/` with the following sample content:

```
clientId=your_client_id_here
privateKey=your_private_key_here
alipayPublicKey=your_public_key_here
gatewayUrl=the_alipay_gateway_endpoint
```

Please see the [developer docs](https://global.alipay.com/doc/ams_upm/introduction) for help with getting the above information.

see `amsSdkDemo.Demo` for more detailed usage.

### 1. To make an user-presented mode In-store payment:

```java


    public static void main(String[] args) {

        //Load the config
        AMSSettings cfg = new AMSSettings();
        
        

        long amountInCents = 1000l;

        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, amountInCents));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        String buyerPaymentCode = "288888888888888888";


        final UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode);

        AMS.with(cfg).execute(request,
        //Handle the API response in a callback, or you can just use the predefined `UserPresentedCodePaymentCallback`
            new Callback<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse>() {

                @Override
                public void onIOException(IOException e, AMSClient client,
                                          UserPresentedCodePaymentRequest request) {
                    //Handle IOException, retry one more time, make an inquiry or make a cancellation.
                }

                @Override
                public void onHttpStatusNot200(AMSClient client,
                                               UserPresentedCodePaymentRequest request, int code) {
                    //Retry one more time, make an inquiry or make a cancellation.
                }

                @Override
                public void onFstatus(AMSClient client, UserPresentedCodePaymentRequest request,
                                      ResponseResult responseResult) {
                    //Check the result code
                }

                @Override
                public void onUstatus(AMSClient client, UserPresentedCodePaymentRequest request,
                                      ResponseResult responseResult) {
                    //Payment is still under processing...  
                    //Schedule a later inquiry
                }

                @Override
                public void onSstatus(AMSClient client, String requestURI,
                                      ResponseHeader responseHeader, HashMap<String, Object> body,
                                      UserPresentedCodePaymentRequest request) {
                    UserPresentedCodePaymentResponse paymentResponse = new UserPresentedCodePaymentResponse(
                        cfg, requestURI, responseHeader, body);
                    PaymentResultModel resultModel = paymentResponse.getPaymentResultModel();
                    //... Get payment result info from resultModel
                    //...

                }
            });

    }
    
```

### 2. To make an order code mode In-store payment:

```java


    public static void main(String[] args) {

        //Load the config
        AMSSettings cfg = new AMSSettings();

        long amountInCents = 1000l;

        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, amountInCents));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();


        AMS.with(cfg).execute(
            new OrderCodePaymentRequest(cfg, paymentRequestId, order, currency, amountInCents),
            new Callback<OrderCodePaymentRequest, OrderCodePaymentResponse>() {

                @Override
                public void onIOException(IOException e, AMSClient client,
                                          OrderCodePaymentRequest request) {
                }

                @Override
                public void onHttpStatusNot200(AMSClient client, OrderCodePaymentRequest request,
                                               int code) {
                }

                @Override
                public void onFstatus(AMSClient client, OrderCodePaymentRequest request,
                                      ResponseResult responseResult) {
                }

                @Override
                public void onUstatus(AMSClient client, OrderCodePaymentRequest request,
                                      ResponseResult responseResult) {
                }

                @Override
                public void onSstatus(AMSClient client, String requestURI,
                                      ResponseHeader responseHeader, HashMap<String, Object> body,
                                      OrderCodePaymentRequest request) {
                    OrderCodePaymentResponse orderCodePaymentResponse = new OrderCodePaymentResponse(
                        requestURI, cfg, responseHeader, body);
                    String codeValueText = orderCodePaymentResponse.getCodeValueText();
                    //Now generate the QR Code using this codeValueText for the buyer to scan and proceed with the payment.
                    //...
                }
            });

    }
    
```

### 3. To make an entry code mode In-store payment:

```java


    public static void main(String[] args) {

        //Load the config
        AMSSettings cfg = new AMSSettings();

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

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();

        EntryCodePaymentRequest request = new EntryCodePaymentRequest(cfg, paymentRequestId, order,
            currency, amountInCents);
        request.setPaymentRedirectUrl("https://global.alipay.com/some_redirect");
        request.setPaymentNotifyUrl("https://global.alipay.com/some_notify");
        request.setPaymentExpiryTime(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
        AMS.with(cfg).execute(request,
            new Callback<EntryCodePaymentRequest, EntryCodePaymentResponse>() {

                @Override
                public void onIOException(IOException e, AMSClient client,
                                          EntryCodePaymentRequest request) {
                }

                @Override
                public void onHttpStatusNot200(AMSClient client, EntryCodePaymentRequest request,
                                               int code) {
                }

            @Override
                public void onFstatus(AMSClient client, EntryCodePaymentRequest request,
                                      ResponseResult responseResult) {
                }

                @Override
                public void onUstatus(AMSClient client, EntryCodePaymentRequest request,
                                      ResponseResult responseResult) {
            }

            @Override
                public void onSstatus(AMSClient client, String requestURI,
                                      ResponseHeader responseHeader, HashMap<String, Object> body,
                                      EntryCodePaymentRequest request) {
                    EntryCodePaymentResponse entryCodePaymentResponse = new EntryCodePaymentResponse(
                        requestURI, cfg, responseHeader, body);
                    String redirectUrl = entryCodePaymentResponse.getRedirectUrl();
                    //Now redirect the buyer to `redirectUrl` and proceed with the payment.
                    //...
            }
        });

    }
    
```

### 4. To make a transaction inquiry:

```java

        AMS.with(cfg).execute(
            PaymentInquiryRequest.byPaymentRequestId(cfg, "PR20190000000001_1571936707820"),
            new Callback<PaymentInquiryRequest, PaymentInquiryResponse>() {

                @Override
                public void onIOException(IOException e, AMSClient client,
                                          PaymentInquiryRequest request) {
                    //Schedule a later inquiry
                }

                @Override
                public void onHttpStatusNot200(AMSClient client, PaymentInquiryRequest request,
                                               int code) {
                    //Schedule a later inquiry
                }

                @Override
                public void onFstatus(AMSClient client, PaymentInquiryRequest request,
                                      ResponseResult responseResult) {
                    //Check the result code                    
                }

                @Override
                public void onUstatus(AMSClient client, PaymentInquiryRequest request,
                                      ResponseResult responseResult) {
                    //Schedule a later inquiry
                }

                @Override
                public void onSstatus(AMSClient client, String requestURI,
                                      ResponseHeader responseHeader, HashMap<String, Object> body,
                                      PaymentInquiryRequest request) {
                    PaymentInquiryResponse inquiryResponse = new PaymentInquiryResponse(requestURI,
                        cfg, responseHeader, body);
                    PaymentResultModel resultModel = inquiryResponse.getPaymentResultModel();
                    //... Get payment result info from resultModel
                    //...
                }
            });
    
    
```
### 5. To make a refund:

```java

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
    
    
```

### 6. To make a cancellation:

```java

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
    
```

### 7. To process a notify SPI request:

```java

        String requestURI = null;
        Map<String, String> notifyRequestHeaders = null; //Request header from Alipay notify
        byte[] notifyRequestBodyContent = null; //Request body from Alipay notify

        // Upon receiving the Notify request from Alipay, call this method to decode the headers and body content.
        // Then get the responseHeaders and responseContent to be write back to the Http response to Alipay.  
        // 
        NotifyResponse notifyResponse = AMS.with(cfg).onNotify(requestURI, notifyRequestHeaders,
            notifyRequestBodyContent,
            new NotifyCallback(allInOne) {

                @Override
                protected void onPaymentSuccess(AMSSettings settings,
                                                NotifyRequestHeader notifyRequestHeader,
                                                PaymentResultModel paymentResultModel) {
                    String paymentId = paymentResultModel.getPaymentId();

                }


                /** 
                 * On a new merchant authorization notify in ISV mode.
                 */
                @Override
                protected void onAuthNotify(AMSSettings settings,
                                            NotifyRequestHeader notifyRequestHeader,
                                            AuthNotifyModel authNotifyModel) {
                }
            });

        Map<String, String> responseHeaders = notifyResponse.getResponseHeaders();
        byte[] responseContent = notifyResponse.getBodyContent();

        //Now write responseHeaders and responseContent back to the Http response to Alipay.
        /*
         * ... httpResponse.setHeaders(responseHeaders);
         * ... httpResponse.write(responseContent);
         */
    
    
```

## Advanced Topic

### Integration Best Practice

Create optimal payment experiences for your customers by following these [best practices](https://global.alipay.com/doc/ams_upm/bp) for integrations.

Also, for user-presented mode In-store payment, we provide a reference implementation following these best practice. See `com.alipay.ams.callbacks.UserPresentedCodePaymentCallback`, `com.alipay.ams.callbacks.PaymentInquiryCallback`, `com.alipay.ams.callbacks.PaymentCancelCallback` and `com.alipay.ams.job.JobExecutor` for details.

```java

//Enable the backend JobExecutor
{
    JobExecutor.instance.setClient(AMS.with(cfg));
    JobExecutor.instance.setJobSupport(jobSupport);
    JobExecutor.instance.setPaymentInquiryCallback(paymentInquiryCallback);

    JobExecutor.instance.startJobExecutor();
}


//Use UserPresentedCodePaymentCallback to handle all the underlying logic.
AMS.with(cfg)
            .execute(request, new UserPresentedCodePaymentCallback(paymentInquiryCallback));

```

### Using API Mock

We provide an API mocking tool(currently in BETA version) for you to easily test exceptional cases. 

Below are some of the default out-of-box mocking rules that basically use the payment amount value to identify the desired mock response:

|API|when which input parameter|equals what|then you get a response of|
|---|---|---|---|
|ams/api/v1/payments/pay|payToAmount.value|9901|UNKNOWN_EXCEPTION|
|ams/api/v1/payments/pay|payToAmount.value|9902|network timeout|
|ams/api/v1/payments/inquiryPayment|payToAmount.value of the corresponding PAY request|9903|UNKNOWN_EXCEPTION|
|ams/api/v1/payments/inquiryPayment|payToAmount.value of the corresponding PAY request|9904|network timeout|

To use this mocking tool:

1. Set gatewayUrl=https://isandbox.alipaydev.com
2. Set alipayPublicKey to a fixed value that you can get from us through sandbox_service@alibaba-inc.com.

### Acceptance testing

Pass all the acceptance test cases in the Alipay Developer Center to ensure a high quality integration. Especially, test exceptions by using test cases.

### Use your customized HTTP client instead of `com.alipay.ams.ApacheHttpPostAMSClient`

You can use whatever is your choice of HTTP client to initiate the requests by,

1. Provide your own implementation of `com.alipay.ams.AMSClient`, or
2. Using your own HTTP client that is totally irrelevant to `com.alipay.ams.AMSClient`.

```java
//To get the body content and http headers to be posted to AMS gateway
String body = request.buildBody().toString();
byte[] requestContent = body.getBytes("UTF-8");

RequestHeader requestHeader = request.buildRequestHeader();

//Now construct your specific HTTP request using requestContent as the body content and requestHeader as the HTTP headers.
//...


```

## To get help

If you have any question or feedbacks regarding this sdk, please contact us at sandbox_service@alibaba-inc.com.

For other tech integration related issues, please reach us through overseas_support@service.alibaba.com. 


## FAQ

### What if I only need to use the digital signature feature ?

See [Digital signature](https://global.alipay.com/doc/ams/digital_signature) for details about the signature algorithm used for data transmission.

`com.alipay.ams.util.SignatureUtil` provides static utility methods that you can directly use.

```java
com.alipay.ams.util.SignatureUtil.sign(String requestURI, String clientId, String requestTime,
                              String privateKey, String requestBody);

com.alipay.ams.util.SignatureUtil.verify(String requestURI, String clientId, String reponseTime,
                                 String alipayPublicKey, String responseBody,
                                 String signatureToBeVerified);

```

## Change history

|Date|Version|Content|Backward compatible?|
|---|---|---|---|
|2020/03/30|2.0.0|Re-designed Callback to enable simple use mode of sdk. |NO|
|2020/03/16|1.4.1|Add support for grossSettlementAmount and settlementQuote|YES|
|2020/02/25|1.3.0|Add request parameters: paymentRedirectUrl, paymentExpiryTime|YES|
|2020/02/21|1.2.0|Add Entry Code Payment support.|YES|

