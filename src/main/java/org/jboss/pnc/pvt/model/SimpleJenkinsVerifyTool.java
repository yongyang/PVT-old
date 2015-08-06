package org.jboss.pnc.pvt.model;

/**
 * A jenkins verify tool to call a defined jekins job
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class SimpleJenkinsVerifyTool extends VerifyTool {

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    protected VerifyResult verify(VerifyParameter param) {
        //TODO: call Jenkins Executor
        return null;
    }
}
