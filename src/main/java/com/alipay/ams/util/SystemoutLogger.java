/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.util;

/**
 * 
 * @author guangling.zgl
 * @version $Id: SystemoutLogger.java, v 0.1 2019年10月24日 上午11:24:48 guangling.zgl Exp $
 */
public class SystemoutLogger implements Logger {

    /** 
     * @see com.alipay.ams.util.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    /** 
     * @see com.alipay.ams.util.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    /** 
     * @see com.alipay.ams.util.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}
