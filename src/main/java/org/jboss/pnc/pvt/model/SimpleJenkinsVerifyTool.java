package org.jboss.pnc.pvt.model;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.wicket.PVTApplication;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * A jenkins verify tool to call a defined/existed jenkins job
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = SimpleJenkinsVerifyTool.class) })
public class SimpleJenkinsVerifyTool extends VerifyTool {

    private static final long serialVersionUID = -8291271547157028632L;

    private static final Logger logger = Logger.getLogger(SimpleJenkinsVerifyTool.class);

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public final Verification verify(VerifyParameter param) {
        final Execution execution = createJenkinsExecution(param);
        final Verification verification = getOrCreateVerification(param, execution);
        if (Verification.Status.PASSED.equals(verification.getStatus())) {
            // has passed already, should we re start it again?
            if (Boolean.valueOf(param.getProperty(VerifyParameter.SKIP_PASSED, "False"))) {
                return verification;
            }
        }
        verification.setStartTime(System.currentTimeMillis());
        doExecute(execution, verification);
        return verification;
    }

    protected void doExecute(final Execution execution, final Verification verification) {
        try {
            verification.setStatus(Verification.Status.IN_PROGRESS); //TODO: when to save the status
            Executor.getJenkinsExecutor().execute(execution, defaultVerificationCallBack(verification));
        } catch (ExecutionException e) {
            verification.setStatus(Verification.Status.NOT_PASSED);
            logger.error("Failed to execute the Jenkins job: " + execution.getName(), e);
        }
    }

    protected Execution createJenkinsExecution(VerifyParameter param) {
        return Execution.createJenkinsExecution(getJobName(param));
    }

    protected String getJobName(VerifyParameter param) {
        String jobId = getJobId();
        if (jobId != null && jobId.trim().length() > 0) {
            return jobId;
        }
        String releaseName = param.getRelease().getName();
        String prdId = param.getRelease().getProductId();
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        String prdName = pvtModel.getProductById(prdId).getName();
        return prdName + "-" + releaseName + "-" + getName();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [name=" + getName() + ", jobId=" + getJobId() + "]";
    }

}
