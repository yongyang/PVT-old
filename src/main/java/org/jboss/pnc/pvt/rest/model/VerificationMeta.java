package org.jboss.pnc.pvt.rest.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A POJO represents the verification status.
 *
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@JsonAutoDetect
public class VerificationMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String waiveComment;
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public VerificationMeta setStatus(String status) {
        this.status = status;
        return this;
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
    public VerificationMeta setWaiveComment(String waiveComment) {
        this.waiveComment = waiveComment;
        return this;
    }

}
