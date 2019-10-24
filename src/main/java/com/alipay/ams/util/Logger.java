/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.util;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Logger.java, v 0.1 2019年10月24日 上午11:12:50 guangling.zgl Exp $
 */
public interface Logger {

    /**
     * 
     * @param string
     * @param string2
     * @param string3
     */
    void debug(String format, Object... args);

    void warn(String format, Object... args);

}
