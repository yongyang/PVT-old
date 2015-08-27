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
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Verification<T extends Serializable> implements Serializable{

    private String id = UUID.randomUUID().toString();
    private String toolId;

    private String referenceReleaseId;
    private String releaseId;

    private long startTime = System.currentTimeMillis();
    private Status status = Status.IN_PROGRESS;

    private Exception exception;

    private T resultObject;

    public Verification() {

    }

    public void setReferenceReleaseId(String referenceReleaseId) {
        this.referenceReleaseId = referenceReleaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

/*
    public Verification(String toolId, String referenceReleaseId, String releaseId) {
        this.toolId = toolId;
        this.referenceReleaseId = referenceReleaseId;
        this.releaseId = releaseId;
    }
*/

    public String getReferenceReleaseId() {
        return referenceReleaseId;
    }

    public String getReleaseId() {
        return releaseId;
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

    public void setResultObject(T resultObject) {
        this.resultObject = resultObject;
    }

    public T getResultObject() {
        return resultObject;
    }

    /**
     * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
     */
    public static enum Status {
        NEW, // Add tool, but not start to verify
        IN_PROGRESS,
        PASSED,
        NOT_PASSED,
        NEED_INSPECT;
    }
}
