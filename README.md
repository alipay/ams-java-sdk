# Alipay AMS Java Bindings [![Build Status]()



## Requirements

Java 1.8 or later.

## Installation

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.alipay.ams</groupId>
  <artifactId>ams-java</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.alipay.ams:ams-java:1.0-SNAPSHOT"
```

## Documentation

Please see the [Java API docs](https://#) for the most up-to-date documentation.

## Usage

### Preparing config.properties

Put a file `config.properties` in your `src/main/resources/` with the following sample content:

```
clientId=your_client_id_here
privateKey=your_private_key_here
alipayPublicKey=your_public_key_here
gatewayUrl=the_alipay_gateway_endpoint
```

Please see the [developer docs](https://#) for help with getting the above information.

#### 1. To make a payment:

```java


    public static void main(String[] args) {

        //Load the config
        AMSSettings cfg = new AMSSettings();

        Order order = new Order();
        Currency currency = Currency.getInstance("USD");
        order.setOrderAmount(new Amount(currency, 1000l));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");

        String paymentRequestId = "PR20190000000001";
        String buyerPaymentCode = "288888888812345678";
        long amountInCents = 1000l;

        UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode);

        AMS.with(cfg).execute(request, new UserPresentedCodePaymentRequestCallback() {

            @Override
            public void onDirectSuccess(UserPresentedCodePaymentResponse paymentResponse) {
                String alipayPaymentId = paymentResponse.getPaymentId();
                String paymentTime = paymentResponse.getPaymentTime();
                String pspCustomerId = paymentResponse.getPspCustomerId();

                //Now payment is done successfully. 
                //Go ahead let POS device print the receipt.....
            }

            @Override
            public void onInprocessing() {
                //Request initiated, but didn't get the final result yet. The buyer might be submitting his/her payment password as required on Alipay APP
                //Poll the result using the inquery API ...

            }

            @Override
            public void onFailure(ResponseResult responseResult) {
                //Payment failed. 
                //Display failure message on the POS device...
            }

            @Override
            public void onCancelled() {

                //Payment cancelled after result polling timeout. Display failure message on the POS device
                //...

            }
        });

    }
    
```

#### 2. To make a inquiry:

```java
String a = null;
```
#### 3. To make a refund:

```java
String a = null;
```

#### 1. To make a cancellation:

```java
String a = null;
```


See the project's [functional tests](https://#) for more examples.

### Configuring Timeouts
