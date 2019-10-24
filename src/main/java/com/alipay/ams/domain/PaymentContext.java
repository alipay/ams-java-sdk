/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.ams.domain;

import java.util.Date;

/**
 * 
 * Context info for a payment.
 * 
 * 
 * @author guangling.zgl
 * @version $Id: PaymentContext.java, v 0.1 2019年10月23日 下午3:52:13 guangling.zgl Exp $
 */
public class PaymentContext {

    /**
     * 
     * @return
     */
    public Date getLastInquiryTime() {
        return null;
    }

    /**
     * 
     * @return
     */
    public int getInquiryCount() {
        return 0;
    }

    /**
     * 
     * @return
     */
    public int getCancelCount() {
        return 0;
    }

}
