package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.*;

import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.wicket.PVTApplication;

/**
 * The base verify tool definition.
 * 
 * Each sub class is recommended to have a 'LABEL' class variant to distinguish it from other tools.
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class VerifyTool implements Serializable {

    private static final long serialVersionUID = -5353557149342108021L;

    private String id = UUID.randomUUID().toString();

    private String name;

    private String description;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VerifyTool other = (VerifyTool) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", name=" + name + "]";
    }

    /**
     * Do verify, normally the worker need to be run in a separate thread
     *
     * @param param @return result, maybe a handle for asynchronous job, such a jenkins job
     */
    public abstract Verification verify(VerifyParameter param);

    /**
     * New Default Verification.
     * 
     * After this method returns, the Verification has been persisted in DB model.
     * 
     * @param param the VerifyParam
     * @param execution the Execution
     * @return a new Verification instance
     */
    public Verification newDefaultVerification(VerifyParameter param, Execution execution) {
        Verification verification = new Verification();
        verification.setReleaseId(param.getRelease().getId());
        if (param.getReferenceRelease() != null) {
            verification.setReferenceReleaseId(param.getReferenceRelease().getId());
        }
        verification.setToolId(param.getToolId());
        verification.setExecution(execution);
//        saveInDB(verification); // do save in ReleasePage for all created verifications together
        return verification;
    }

/*
    private void saveInDB(Verification verification) {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        pvtModel.addVerification(verification);
    }
*/

    protected Verification getLastVerification(String releaseId) {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        Optional<Verification> optional = pvtModel.getVerificationsList().stream()
                .filter(p -> p.getReleaseId().equals(releaseId) && p.getToolId().equals(getId())).limit(1L).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    protected Verification getOrCreateVerification(VerifyParameter param, Execution execution) {
        Verification verification = getLastVerification(param.getRelease().getId());
        if (verification != null) {
            verification.setExecution(execution); // set to the new Execution for existed Verification
            return verification;
        }
        return newDefaultVerification(param, execution);
    }

    protected String getProductName(String productId) {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        Product prd = pvtModel.getProductById(productId);
        return prd == null ? null : prd.getName();
    }

    protected String getVerifiedProductName(VerifyParameter param) {
        String prdId = param.getRelease().getProductId();
        return getProductName(prdId);
    }

    protected CallBack defaultVerificationCallBack(final Verification verification) {
        return new CallBack() {

            @Override
            public void onStatus(Execution execution) {
                verification.syncStauts();
                PVTApplication.getDAO().persist();
            }

            @Override
            public void onTerminated(Execution execution) {
                verification.setEndTime(System.currentTimeMillis());
                PVTApplication.getDAO().persist();
            }

            @Override
            public void onLogChanged(Execution execution) {
                // nothing to do for now ...
            }

        };
    }
}
