/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain.telemetry;

/**
 * 
 * @author guangling.zgl
 * @version $Id: Cancel.java, v 0.1 2019年11月4日 下午3:46:50 guangling.zgl Exp $
 */
public class Cancel extends Call {

    /**  */
    private static final long serialVersionUID = 8466990858832870946L;

    /**
     * @param api
     */
    public Cancel() {
        super("cancel");
    }

}
