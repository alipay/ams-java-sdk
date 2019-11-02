/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.cfg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.alipay.ams.util.Logger;
import com.alipay.ams.util.SystemoutLogger;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMSSettings.java, v 0.1 2019年8月26日 下午6:32:56 guangling.zgl Exp $
 */
public class AMSSettings {

    //from config.properties start
    public String  clientId;
    public String  privateKey;
    public String  alipayPublicKey;
    public String  gatewayUrl;
    //from config.properties end

    public String  readTimeout;
    public String  connTimeout;

    public int[]   inquiryInterval                         = new int[] { 2, 3, 3, 5, 5, 5, 5, 5, 5 };
    public int[]   cancelInterval                          = new int[] { 2, 3, 3, 5, 5 };

    public boolean enableTelemetry                         = true;
    public int     maxInquiryCount                         = inquiryInterval.length;
    public int     maxCancelCount                          = cancelInterval.length;

    public Logger  logger                                  = new SystemoutLogger();
    public long    jobListingDelayInMilliSeconds           = 300;
    public int     corePoolSizeOfJobExecutor               = 10;
    public long    lockAutoReleaseDelayInMilliSeconds      = 300;
    public int     retryHandlePaymentSuccessDelayInSeconds = 3;

    public AMSSettings() {

        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
                this.clientId = prop.getProperty("clientId");
                this.privateKey = prop.getProperty("privateKey");
                this.alipayPublicKey = prop.getProperty("alipayPublicKey");
                this.gatewayUrl = prop.getProperty("gatewayUrl");
            } else {
                throw new FileNotFoundException(
                    String
                        .format(
                            "property file '%s' not found in the classpath [src/main/resources/config.properties]",
                            propFileName));
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //
                }
            }
        }

    }

    public boolean validate() {
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean isDevMode() {
        return gatewayUrl.contains("dev.alipay.net") || gatewayUrl.contains(".alipaydev.com");
    }
}