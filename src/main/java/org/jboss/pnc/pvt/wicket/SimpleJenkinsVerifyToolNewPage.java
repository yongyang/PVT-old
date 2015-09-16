package org.jboss.pnc.pvt.wicket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.dom4j.DocumentException;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob.SerializableStringParam;
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

    private final ListView<SerializableStringParam> paramsListView;

    private final Label archiverLabel;

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
        final MarkupContainer stringParamsPanel = new WebMarkupContainer("stringParamsPanel");
        stringParamsPanel.setOutputMarkupId(true);

        paramsListView = new ListView<SerializableStringParam>("stringParams", new ArrayList<>()) {

            @Override
            protected void populateItem(final ListItem<SerializableStringParam> item) {
                item.add(new Label("name", item.getModelObject().getName()));
                item.add(new Label("description", item.getModelObject().getDescription()));
                item.add(new Label("defaultValue", item.getModelObject().getDefaultValue()));
            }
        };
        stringParamsPanel.add(paramsListView);
        form.add(stringParamsPanel);

        archiverLabel = new Label("archiver", "");
        archiverLabel.setOutputMarkupId(true);
        form.add(archiverLabel);

        IndicatingAjaxLink<String> checkJobLink = new IndicatingAjaxLink<String>("checkJob") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    String jobId = jobIdTxtField.getValue();
                    JenkinsServer server = Executor.getJenkinsServer(JenkinsConfiguration.defaultJenkinsProps());
                    // check job string parameters
                    ParamJenkinsJob paramJob = new ParamJenkinsJob(server.getJobXml(jobId));
                    List<SerializableStringParam> stringParams = paramJob.getStringParams();
                    paramsListView.setModelObject(stringParams);

                    // check job archiver
                    String archiver = paramJob.getArchiver();
                    if (archiver != null) {
                        archiverLabel.setDefaultModelObject(archiver);
                    } else {
                        archiverLabel.setDefaultModelObject("No Archive specified!");
                    }
                } catch (IOException | DocumentException e) {
                    setInfo(e.getMessage());
                }
                if (target != null) {
                    target.add(stringParamsPanel);
                    target.add(archiverLabel);
                }
            }
        };
        form.add(checkJobLink);
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