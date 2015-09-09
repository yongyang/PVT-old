package org.jboss.pnc.pvt.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;

/**
 * A verify tool to call jenkins server with config.xml job template provided
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class TemplateJenkinsVerifyTool extends SimpleJenkinsVerifyTool {
    
    private static final long serialVersionUID = -7165679900379794100L;

    private static final Logger logger = Logger.getLogger(TemplateJenkinsVerifyTool.class);

    private String jenkinsConfigXML;

    public String getJenkinsConfigXML() {
        return jenkinsConfigXML;
    }

    public void setJenkinsConfigXML(String jenkinsConfigXML) {
        this.jenkinsConfigXML = jenkinsConfigXML;
    }

    @Override
    protected Execution createJenkinsExecution(VerifyParameter param) {
        Map<String, String> jobParams = new HashMap<>();
        return Execution.createJenkinsExecution(getJobName(param), getJenkinsConfigXML(), jobParams);
    }

    protected void doExecute(final Execution execution, final Verification verification) {
        try {
            JenkinsConfiguration jenkinsConfig = JenkinsConfiguration.defaultJenkinsProps();
            jenkinsConfig.setCreateIfJobMissing(true);
            jenkinsConfig.setOverrideJob(true);
            verification.setStatus(Verification.Status.IN_PROGRESS);
            Executor.getJenkinsExecutor(jenkinsConfig).execute(execution, defaultVerificationCallBack(verification));
        } catch (ExecutionException | IOException e) {
            verification.setStatus(Verification.Status.NOT_PASSED);
            logger.error("Failed to execute the Jenkins job: " + execution.getName(), e);
        }
    }

}
