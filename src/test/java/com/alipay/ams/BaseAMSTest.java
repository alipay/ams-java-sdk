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

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.mockito.stubbing.OngoingStubbing;

import com.alipay.ams.util.MockSetup;
import com.alipay.ams.util.TestUtil;

/**
 * 
 * @author guangling.zgl
 * @version $Id: BaseAMSTest.java, v 0.1 2020年4月8日 下午2:04:10 guangling.zgl Exp $
 */
public abstract class BaseAMSTest {

    public MockSetup mockSetupWithResponse(String... responseFilePaths)
                                                                       throws UnsupportedOperationException,
                                                                       Exception {
        return mockSetupWithResponse(true, responseFilePaths);
    }

    /**
     * 
     * @param skipSignatureVerification
     * @param responseFilePaths
     * @return
     * @throws UnsupportedOperationException
     * @throws Exception
     */
    public MockSetup mockSetupWithResponse(final boolean skipSignatureVerification,
                                           final String... responseFilePaths) throws Exception {

        return new MockSetup(skipSignatureVerification) {

            @Override
            protected void setup() throws Exception {

                when(closeableHttpClient.execute(httpPostArgumentCaptor.capture())).thenReturn(
                    closeableHttpResponse);

                StatusLine statusLine = new StatusLine() {

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return null;
                    }

                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return null;
                    }
                };
                when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
                when(closeableHttpResponse.getEntity()).thenReturn(entity);

                OngoingStubbing<InputStream> stub = when(entity.getContent());
                for (String filePath : responseFilePaths) {
                    stub = stub
                        .thenReturn(new ByteArrayInputStream(TestUtil.getApiMocks(filePath)));
                }
            }
        };
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public MockSetup mockSetupWithIOException() throws Exception {

        return new MockSetup(true) {

            @Override
            protected void setup() throws Exception {

                when(closeableHttpClient.execute(httpPostArgumentCaptor.capture())).thenThrow(
                    new IOException());
            }
        };

    }

    /**
     * 
     * @return
     * @throws UnsupportedOperationException
     * @throws Exception
     */
    public MockSetup mockSetupWithHttpStatusNot200() throws UnsupportedOperationException,
                                                    Exception {

        return new MockSetup(true) {

            @Override
            protected void setup() throws Exception {

                when(closeableHttpClient.execute(httpPostArgumentCaptor.capture())).thenReturn(
                    closeableHttpResponse);
                StatusLine statusLine = new StatusLine() {

                    @Override
                    public int getStatusCode() {
                        return 300;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return null;
                    }

                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return null;
                    }
                };
                when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
            }
        };

    }
}
