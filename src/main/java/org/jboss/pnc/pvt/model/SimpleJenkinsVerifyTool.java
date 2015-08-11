package org.jboss.pnc.pvt.model;

/**
 * A jenkins verify tool to call a defined/existed jenkins job
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class SimpleJenkinsVerifyTool extends VerifyTool {

    private static final long serialVersionUID = -8291271547157028632L;

    private String jobId;

    public static final String LABEL = "Simple Jenkins Tool";

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    protected <T> VerifyResult<T> verify(VerifyParameter param) {
        //TODO: call Jenkins Executor
        return null;
    }

    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.model.VerifyTool#getLabel()
     */
    @Override
    public String getLabel() {
        return LABEL;
    }

    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.model.VerifyTool#getPageVariant()
     */
    @Override
    public String getPageVariant() {
        return "simplejenkins";
    }
}
