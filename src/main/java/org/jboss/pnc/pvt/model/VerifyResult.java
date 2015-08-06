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

    abstract T getResult();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
