package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by yyang on 4/24/15.
 */
@JsonAutoDetect
public enum  PVTStatus {
    NEW, ANALYZING, VERIFYING, PASSED, REJECTED
}
