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
package com.alipay.ams.cfg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import com.alipay.ams.util.Logger;
import com.alipay.ams.util.SystemoutLogger;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMSSettings.java, v 0.1 2019年8月26日 下午6:32:56 guangling.zgl Exp $
 */
public class AMSSettings implements Serializable {

    /**  */
    private static final long  serialVersionUID                        = -628578313361694675L;

    public static final String sdkVersion                              = "2.2.0.20201102";
    //from config.properties start
    public String              clientId;
    public String              privateKey;
    public String              alipayPublicKey;
    public String              gatewayUrl;
    //from config.properties end

    //Max milliseconds to wait for a connection from the pool
    public int                 apacheHttpConnectionRequestTimeout      = 5000;
    //Max milliseconds to wait for a new connection with Alipay gateway to be established
    public int                 apacheHttpConnectTimeout                = 5000;
    //Max milliseconds to wait for response data
    public int                 apacheHttpSocketTimeout                 = 5000;
    public int                 apacheMaxPoolSize                       = 50;

    public int[]               inquiryInterval                         = new int[] { 2, 3, 3, 5, 5,
            5, 5, 5, 5                                                };
    public int[]               cancelInterval                          = new int[] { 2, 3, 3, 5, 5 };

    public boolean             enableTelemetry                         = true;
    public int                 maxInquiryCount                         = inquiryInterval.length;
    public int                 maxCancelCount                          = cancelInterval.length;
    public int                 maxOrderCodeRequestCount                = 3;
    public int                 maxEntryCodeRequestCount                = 3;

    public Logger              logger                                  = new SystemoutLogger();
    public long                jobListingDelayInMilliSeconds           = 300;
    public int                 corePoolSizeOfJobExecutor               = 10;
    public long                lockAutoReleaseDelayInMilliSeconds      = 300;
    public int                 retryHandlePaymentSuccessDelayInSeconds = 3;

    /**
     * @param clientId
     * @param privateKey
     * @param alipayPublicKey
     * @param gatewayUrl
     */
    public AMSSettings(String clientId, String privateKey, String alipayPublicKey, String gatewayUrl) {
        this.clientId = clientId;
        this.privateKey = privateKey;
        this.alipayPublicKey = alipayPublicKey;
        this.gatewayUrl = gatewayUrl;
    }

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
