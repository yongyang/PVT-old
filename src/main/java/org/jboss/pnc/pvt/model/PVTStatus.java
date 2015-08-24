package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by yyang on 4/24/15.
 */
@JsonAutoDetect
public enum  PVTStatus {
    NEW, VERIFYING, NEED_INSPECT, PASSED, REJECTED
}
