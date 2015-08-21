package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.jboss.pnc.pvt.execution.Execution;

/**
 * A jenkins verify tool to call a defined/existed jenkins job
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = SimpleJenkinsVerifyTool.class)})
public class SimpleJenkinsVerifyTool extends VerifyTool {

    private static final long serialVersionUID = -8291271547157028632L;

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public Verification<Execution> verify(VerifyParameter param) {
        //TODO: call Jenkins Executor
        return null;
    }

}
