package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.dom4j.DocumentException;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob;
import org.jboss.pnc.pvt.model.TemplateJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class TemplateJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    private final TextArea<String> configXMLTxtArea;
    private TextField<String> jobIdTextField;

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        configXMLTxtArea = new TextArea<String>("jenkinsConfigXML") {

        };
        configXMLTxtArea.setLabel(Model.of("Jenkins Config XML"));
        configXMLTxtArea.add(new IValidator<String>() {

            @Override
            public void validate(IValidatable<String> validatable) {
                String xml = validatable.getValue();
                ValidationError error = new ValidationError();
                try {
                    new ParamJenkinsJob(xml);
                } catch (DocumentException e) {
                    error.addKey("xmlInvalid");
                    validatable.error(error);
                    return;
                }
            }
        });
        configXMLTxtArea.setRequired(true);
        form.add(configXMLTxtArea);
        jobIdTextField = new TextField<String>("jobId");
        form.add(jobIdTextField);
        form.add(new Link<String>("varLink") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                String ctxPath = PVTApplication.get().getServletContext().getContextPath();
                tag.put("onclick", "showVariables('" + ctxPath + "');");
            }

            @Override
            public void onClick() {
            }
        });
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
        return new TemplateJenkinsVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a template jenkins verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new TemplateJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}
