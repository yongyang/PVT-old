package org.jboss.pnc.pvt.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by yyang on 4/21/15.
 */
@JsonAutoDetect
public class Report implements Serializable {

    private static final long serialVersionUID = -8706642526036352756L;

    private String mainLog;

    @JsonInclude(Include.NON_EMPTY)
    private List<ReportLog> externalLogs = new ArrayList<>();

    /**
     * @return the mainLog
     */
    public String getMainLog() {
        return mainLog;
    }

    /**
     * @param mainLog the mainLog to set
     */
    public void setMainLog(String mainLog) {
        this.mainLog = mainLog;
    }

    public void addReportLog(ReportLog reportLog) {
        this.externalLogs.add(reportLog);
    }

    /**
     * @return the externalLogs
     */
    public List<ReportLog> getExternalLogs() {
        return externalLogs;
    }

    /**
     * @param externalLogs the externalLogs to set
     */
    public void setExternalLogs(List<ReportLog> externalLogs) {
        this.externalLogs = externalLogs;
    }

    @JsonAutoDetect
    public static class ReportLog implements Serializable {

        private static final long serialVersionUID = -6324864811810082840L;

        private final String name;
        private final String content;

        public ReportLog(final String name, final String content) {
            super();
            this.name = name;
            this.content = content;
        }

        /**
         * @return the name
         */
        public synchronized String getName() {
            return name;
        }

        /**
         * @return the content
         */
        public synchronized String getContent() {
            return content;
        }

    }
}
