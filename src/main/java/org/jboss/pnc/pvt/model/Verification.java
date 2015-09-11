package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.UUID;

import org.jboss.pnc.pvt.execution.Execution;

/**
 * Verification used to track a verify result, return by VerifyTool.verify()
 * Verification will be stored to DB once it created.
 *
 * The executor needs to update Verification once the status updated.
 *
 * The UI will pull Verification from DB to track the status.
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Verification implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id = UUID.randomUUID().toString();
    private String toolId;

    private String referenceReleaseId;
    private String releaseId;

    private long startTime = System.currentTimeMillis();
    private long endTime = 0;
    private Status status = Status.NEW;
    
    private Execution execution;

    /**
     * Waive comment in case this Verification is waived.
     */
    private String waiveComment;

    public Verification() {

    }

    public void setReferenceReleaseId(String referenceReleaseId) {
        this.referenceReleaseId = referenceReleaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getReferenceReleaseId() {
        return referenceReleaseId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getId() {
        return id;
    }

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the execution
     */
    public Execution getExecution() {
        return execution;
    }

    /**
     * @param execution the execution to set
     */
    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    /**
     * @return the waiveComment
     */
    public String getWaiveComment() {
        return waiveComment;
    }

    /**
     * @param waiveComment the waiveComment to set
     */
    public void setWaiveComment(String waiveComment) {
        this.waiveComment = waiveComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Verification that = (Verification) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public synchronized void syncStauts() {
        if (this.execution != null) {
            switch (execution.getStatus()) {
                case RUNNING: {
                    setStatus(Verification.Status.IN_PROGRESS);
                    break;
                }
                case UNKNOWN: {
                    setStatus(Verification.Status.NEED_INSPECT);
                    break;
                }
                case FAILED: {
                    setStatus(Verification.Status.NOT_PASSED);
                    break;
                }
                case SUCCEEDED: {
                    setStatus(Verification.Status.PASSED);
                    break;
                }
            }
        }
    }

    public boolean needWaive() {
        return !(this.getStatus() == Verification.Status.NOT_PASSED
                || getStatus() == Verification.Status.NEED_INSPECT
                || getStatus() == Verification.Status.WAIVED);
    }

    /**
     * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
     */
    public static enum Status {
        NEW, // Add tool, but not start to verify
        IN_PROGRESS,
        PASSED,
        NOT_PASSED,
        NEED_INSPECT,
        WAIVED;
    }
}
