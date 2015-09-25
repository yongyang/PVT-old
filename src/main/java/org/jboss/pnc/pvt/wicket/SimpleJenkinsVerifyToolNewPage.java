package org.jboss.pnc.pvt.wicket;

import java.io.IOException;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;
import org.jboss.pnc.pvt.model.SimpleJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class SimpleJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    private final RequiredTextField<String> jobIdTxtField;

    public SimpleJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public SimpleJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        jobIdTxtField = new RequiredTextField<String>("jobId");
        jobIdTxtField.setLabel(Model.of("Jenkins Job Id"));
        jobIdTxtField.add(new IValidator<String>() {

            @Override
            public void validate(IValidatable<String> validatable) {
                try {
                    String jobId = validatable.getValue();
                    JenkinsServer server = Executor.getJenkinsServer(JenkinsConfiguration.defaultJenkinsProps());
                    ValidationError error = new ValidationError();
                    if (jobId == null || jobId.trim().length() == 0) {
                        error.addKey("jobNull");
                        validatable.error(error);
                        return;
                    }
                    // check job existence
                    JobWithDetails job = server.getJob(jobId);
                    if (job == null) {
                        error.addKey("jobNotExist").setVariable("jobId", jobId);
                        validatable.error(error);
                        return;
                    }
                } catch (IOException e) {
                    jobIdTxtField.error(new ValidationError(e.getMessage()));
                }
            }

        });
        form.add(jobIdTxtField);
        form.add(new Link<String>("checkJob") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                String ctxPath = PVTApplication.get().getServletContext().getContextPath();
                tag.put("onclick", "checkJenkinsJob('" + ctxPath + "');");
            }

            @Override
            public void onClick() {
            }
        });
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new SimpleJenkinsVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a simple jenkins verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new SimpleJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}