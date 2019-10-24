/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams;

import com.alipay.ams.cfg.AMSSettings;

/**
 * 
 * @author guangling.zgl
 * @version $Id: AMS.java, v 0.1 2019年8月26日 下午6:25:48 guangling.zgl Exp $
 */
public class AMS {

    /**
     * 
     * @param cfg
     * @return
     */
    public static AMSClient with(AMSSettings cfg) {
        return new ApacheHttpPostAMSClient(cfg);
    }

}
