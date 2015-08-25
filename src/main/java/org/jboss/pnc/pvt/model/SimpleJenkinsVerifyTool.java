package org.jboss.pnc.pvt.model;

import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * A jenkins verify tool to call a defined/existed jenkins job
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = SimpleJenkinsVerifyTool.class) })
public class SimpleJenkinsVerifyTool extends VerifyTool<Execution> {

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
        final Execution execution = Execution.createJenkinsExecution(getJobId(), null, null);
        //TODO find the Verification from db if it has been executed before ?

        final Verification<Execution> verification = new Verification<Execution>() {

            @Override
            public Execution getResultObject() {
                return execution;
            }
        };

        execution.addCallBack(new Execution.CallBack() {

            @Override
            public void onStatus(Execution execution) {
                // TODO update DB about the execution result !?
            }

            @Override
            public void onLogChanged(Execution execution) {
                // TODO update DB about the execution result !?

            }
        });
        try {
            Executor.getJenkinsExecutor().execute(execution);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return verification;
    }

}
