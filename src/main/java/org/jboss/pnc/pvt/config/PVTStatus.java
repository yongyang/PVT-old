package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by yyang on 4/24/15.
 */
@JsonAutoDetect
public class PVTStatus {

    public static enum Status {
        ANALYZING, VERIFYING, PASSED, REJECTED
    }

}
