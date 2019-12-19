# Alipay AMS Java Bindings 

[![Build Status](https://travis-ci.com/alipay/ams-java-sdk.svg?branch=master)](https://travis-ci.com/alipay/ams-java-sdk)
[![codecov](https://codecov.io/gh/alipay/ams-java-sdk/branch/master/graph/badge.svg)](https://codecov.io/gh/alipay/ams-java-sdk)
![license](https://img.shields.io/badge/license-MIT-green)

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
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.alipay.ams:ams-java:0.1.0-SNAPSHOT"
```

## Documentation

Please see the [Java API docs](#) for the most up-to-date documentation.

## Usage

### Preparing config.properties

Put a file `config.properties` in your `src/main/resources/` with the following sample content:

```
clientId=your_client_id_here
privateKey=your_private_key_here
alipayPublicKey=your_public_key_here
gatewayUrl=the_alipay_gateway_endpoint
```

Please see the [developer docs](#) for help with getting the above information.

see `amsSdkDemo.Demo` for detailed usage.

#### 1. To make a payment:

```java


    public static void main(String[] args) {

        //Load the config
        AMSSettings cfg = new AMSSettings();

        Order order = new Order();
        long amountInCents = 1000l;
        order.setOrderAmount(new Amount(Currency.getInstance("USD"), amountInCents));
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        String buyerPaymentCode = "288888888812345678"; // from the scanner
        
        final UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode);

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
    
```

#### 2. To make an inquiry:

```java

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
    
```
#### 3. To make a refund:

```java

        PaymentRefundRequest paymentRefundRequest = null; // Build the refund request.
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
    
```

#### 4. To make a cancellation:

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

#### 5. To process a notify SPI request:

```java


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
    
```


See the project's [functional tests](#) for more examples.

### Configuring
