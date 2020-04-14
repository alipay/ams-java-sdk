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
package com.alipay.ams.domain;

import java.io.IOException;
import java.util.HashMap;

import com.alipay.ams.AMSClient;
import com.alipay.ams.callbacks.TelemetrySupport;
import com.alipay.ams.domain.telemetry.DummyTelemetrySupport;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Callback.java, v 0.1 2019年10月16日 下午7:48:32 guangling.zgl Exp $
 */
public abstract class Callback<R extends Request, P extends Response> {

    private TelemetrySupport<R, P> telemetrySupport;

    /**
     * Use this constructor if you are to use the TelemetrySupport feature.
     * 
     * @param paymentContextSupport
     */
    public Callback(TelemetrySupport<R, P> telemetrySupport) {

        this.telemetrySupport = telemetrySupport;
    }

    /**
     * Use this constructor if you are not to use the TelemetrySupport feature.
     * 
     */
    public Callback() {

        //Use a dummy one.
        this.telemetrySupport = new DummyTelemetrySupport<R, P>();
    }

    /**
     * 
     * @param e
     * @param client
     * @param request
     */
    public abstract void onIOException(IOException e, AMSClient client, R request);

    /**
     * 
     * @param code
     */
    public abstract void onHttpStatusNot200(AMSClient client, R request, int code);

    /**
     * 
     * @param request 
     * @param responseHeader
     * @param responseBody
     */
    public void onSignatureVerifyFailed(R request, ResponseHeader responseHeader,
                                        String responseBody) {
        throw new IllegalStateException(String.format(
            "SignatureVerifyFailed[%s]: [bizId=[%s]], responseBody=[%s]", request.getClass()
                .getName(), request.getBizIdentifier(), responseBody));
    }

    /**
     * 
     * @param responseResult
     */
    public abstract void onFstatus(AMSClient client, R request, ResponseResult responseResult);

    /**
     * 
     * @param responseResult
     * @param client
     * @param request
     */
    public abstract void onUstatus(AMSClient client, R request, ResponseResult responseResult);

    /**
     * 
     * @param settings
     * @param requestURI
     * @param responseHeader
     * @param body
     */
    public abstract void onSstatus(AMSClient client, String requestURI,
                                   ResponseHeader responseHeader, HashMap<String, Object> body,
                                   R request);

    /**
     * Getter method for property <tt>telemetrySupport</tt>.
     * 
     * @return property value of telemetrySupport
     */
    public TelemetrySupport<R, P> getTelemetrySupport() {
        return telemetrySupport;
    }

    /**
     * Setter method for property <tt>telemetrySupport</tt>.
     * 
     * @param telemetrySupport value to be assigned to property telemetrySupport
     */
    public void setTelemetrySupport(TelemetrySupport<R, P> telemetrySupport) {
        this.telemetrySupport = telemetrySupport;
    }

}
