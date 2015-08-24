package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.UUID;

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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class Verification<T extends Serializable> {

    private String id = UUID.randomUUID().toString();
    private String toolId;

    private String previousReleaseId;
    private String currentReleaseId;

    private long startTime;
    private Status status = Status.IN_PROGRESS;

    private Exception exception;

    public Verification(String toolId, String previousReleaseId, String currentReleaseId) {
        this.toolId = toolId;
        this.previousReleaseId = previousReleaseId;
        this.currentReleaseId = currentReleaseId;
    }

    public String getPreviousReleaseId() {
        return previousReleaseId;
    }

    public String getCurrentReleaseId() {
        return currentReleaseId;
    }

    public String getId() {
        return id;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract T getResultObject();

    /**
     * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
     */
    public static enum Status {
        NEW, // Add tool, but not start to verify
        IN_PROGRESS,
        PASSED,
        REJECTED,
        NEED_INSPECT;
    }
}
