package org.jboss.pnc.pvt.model;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob;
import org.jboss.pnc.pvt.wicket.PVTApplication;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.StringParameterDefinition;

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
        Map<String, String> jobParams = getJobParams(getJobName(param), param);
        return Execution.createJenkinsExecution(getJobName(param), jobParams);
    }

    private Map<String, String> getJobParams(String jobName, VerifyParameter verifyParam) {
        try {
            JenkinsServer jenkinsServer = Executor.getJenkinsServer(JenkinsConfiguration.defaultJenkinsProps());
            if (jenkinsServer.getJob(jobName) == null) {
                return Collections.emptyMap();
            }
            String jobXml = jenkinsServer.getJobXml(jobName);
            if (jobXml == null) {
                return Collections.emptyMap();
            }
            ParamJenkinsJob paramJob = new ParamJenkinsJob(jobXml);
            List<StringParameterDefinition> stringParams = paramJob.getStringParams();
            Map<String, String> params = new HashMap<>();
            for (StringParameterDefinition spd: stringParams) {
                params.put(spd.getName(), verifyParam.getProperty(spd.getName(), spd.getDefaultValue()));
            }
            return params;
        } catch (IOException e) {
            logger.warn("Can't get parameters of job: " + jobName, e);
        } catch (DocumentException e) {
            logger.warn("Invalid Jenkins Job XML of job: "  + jobName, e);
        }
        return Collections.emptyMap();
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
