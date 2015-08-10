package org.jboss.pnc.pvt.model;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public abstract class VerifyResult<T> {

    public static enum Status {
        IN_PROGRESS,
        NEED_INSPECT,
        PASSED,
        REJECTED
    }

    private Status status = Status.IN_PROGRESS;

    private Exception exception;

    public abstract T getResult();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
