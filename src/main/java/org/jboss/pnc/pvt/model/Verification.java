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
public class Verification<T extends Serializable> {

    private String id = UUID.randomUUID().toString();
    private String toolId;

    private long startTime;
    private VerifyStatus status = VerifyStatus.IN_PROGRESS;

    private Exception exception;

    private T resultObject;

    public Verification() {
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

    public T getResultObject() {
        return resultObject;
    }

    public void setResultObject(T resultObject) {
        this.resultObject = resultObject;
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

    public VerifyStatus getStatus() {
        return status;
    }

    public void setStatus(VerifyStatus status) {
        this.status = status;
    }
}