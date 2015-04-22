package org.jboss.pnc.pvt.config;

import org.jboss.pnc.pvt.report.Report;

/**
 * A runtime Jenkins Job
 *
 * Created by yyang on 4/22/15.
 */
public class RunningJob {

    public static enum Status {
        STOP, RUNNING, FINISHED
    }

    /**
     * Jenkins jobId
     */
    private String JobId;

    /**
     * Jenkins buildId
     */
    private int buildId;



    private Status status;


}
