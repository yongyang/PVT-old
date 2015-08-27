package org.jboss.pnc.pvt.model;

import org.apache.wicket.Application;
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
public class SimpleJenkinsVerifyTool extends VerifyTool<Execution> {

    private static final long serialVersionUID = -8291271547157028632L;

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @SuppressWarnings("serial")
    @Override
    public Verification<Execution> verify(VerifyParameter param) {
        final Execution execution = createJenkinsExecution(param);

        //TODO find the Verification from db if it has been executed before ?
        final Verification<Execution> verification = newDefaultVerification(param, execution);
        verification.setStartTime(System.currentTimeMillis());
        verification.setResultObject(execution);

        execution.addCallBack(new Execution.CallBack() {

            @Override
            public void onStatus(Execution execution) {
                updateInDB(verification);
            }

            @Override
            public void onLogChanged(Execution execution) {
                updateInDB(verification);
            }

            @Override
            public void onException(Execution execution) {
                updateInDB(verification);
            }
        });
        doExecute(execution, verification);
        return verification;
    }

    protected void doExecute(final Execution execution, final Verification<Execution> verification) {
        try {
            Executor.getJenkinsExecutor().execute(execution);
            verification.setStatus(Verification.Status.IN_PROGRESS);
        } catch (ExecutionException e) {
            verification.setException(e);
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
        String releaseName = param.getCurrentRelease().getName();
        String prdId = param.getCurrentRelease().getProductId();
        PVTModel pvtModel = ((PVTApplication) Application.get()).getDAO().getPvtModel();
        String prdName = pvtModel.getProductbyId(prdId).getName();
        return prdName + "-" + releaseName + "-" + getName();
    }

    protected void updateInDB(Verification<Execution> verification) {
        Execution exec = verification.getResultObject();
        switch (exec.getStatus()) {
            case RUNNING:
            {
                verification.setStatus(Verification.Status.IN_PROGRESS);
                break;
            }
            case FAILED:
            case UNKNOWN:
            {
                verification.setStatus(Verification.Status.NEED_INSPECT);
                break;
            }
            case SUCCEEDED:
            {
                verification.setStatus(Verification.Status.PASSED);
                break;
            }
        }
        PVTModel pvtModel = ((PVTApplication) Application.get()).getDAO().getPvtModel();
//        pvtModel.updateVerification(verification); TODO
//        ((PVTApplication) Application.get()).getDAO().persist();
    }

}
