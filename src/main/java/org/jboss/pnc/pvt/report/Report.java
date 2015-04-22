package org.jboss.pnc.pvt.report;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by yyang on 4/21/15.
 */
@JsonAutoDetect
public abstract class Report {

    private String jobId;
    private int buildId;

    private String content;

    public static enum Result {
        PASSED, NOT_PASS, NOT_DETERMINED
    }

}
