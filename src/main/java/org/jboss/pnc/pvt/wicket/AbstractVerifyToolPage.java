package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@SuppressWarnings("serial")
public abstract class AbstractVerifyToolPage extends TemplatePage {

    protected Form<VerifyTool> form;

    protected Button resetButton;
    protected Button removeButton;

    protected VerifyTool tool;

    public AbstractVerifyToolPage(PageParameters pp) {
        this(pp, null);
    }

    
    public AbstractVerifyToolPage(final PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu(Menu.TOOLS);

        add(new FeedbackPanel("feedbackMessage"));

        add(new Label("tool_summary", getTitle()));

        tool = getVerifyTool(pp);

        form = new Form<VerifyTool>("form-tool", new CompoundPropertyModel<VerifyTool>(tool)) {
            @Override
            protected void onSubmit() {
                doSubmit(pp);
            }

        };

        // adds common fields
        final RequiredTextField<String> toolNameTextField = new RequiredTextField<String>("name");
        toolNameTextField.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                String toolName = validatable.getValue();
                PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
                if (pvtModel.getToolsList().stream().anyMatch(p -> p.getName().equals(toolName))
                        && !toolName.equals(tool.getName())) {
                    ValidationError error = new ValidationError();
                    error.addKey("toolName.unique").setVariable("toolName", toolName);
                    validatable.error(error);
                }
            }
        });
        form.add(toolNameTextField);

        form.add(new TextArea<String>("description"));

        // adds buttons
        Button backButton = new Button("back") {

            @Override
            public void onSubmit() {
                PageParameters pp = new PageParameters();
                setResponsePage(ToolsPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);
        form.add(backButton);

        resetButton = new Button("reset") {
            @Override
            public void onSubmit() {
                doReset(pp);
            }
        };
        resetButton.setDefaultFormProcessing(false);

        form.add(resetButton);

        removeButton = new Button("remove") {

            @Override
            public void onSubmit() {
                doRemove(form.getModelObject());
                PageParameters pp = new PageParameters();
                setResponsePage(new ToolsPage(pp, "Tool: " + form.getModelObject().getName() + " is removed."));
            }

        };
        removeButton.setDefaultFormProcessing(false);
        // removeButton.setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
        form.add(removeButton);

        add(form);
    }

    @Override
    protected void onConfigure() {
        removeButton.setVisible(false);
    }

    protected abstract VerifyTool getVerifyTool(PageParameters pp);

    protected abstract void doSubmit(PageParameters pp);

    protected void doReset(PageParameters pp) {
        form.getModel().setObject(getVerifyTool(pp));
    }

    protected void doRemove(VerifyTool tool) {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        pvtModel.getTools().entrySet().removeIf(p -> p.getValue().equals(tool));
    }

    protected String getTitle() {
        return "Define A JDK compatible verify tool";
    }

    protected VerifyTool searchVerifyTool(PageParameters pp) {
        if (pp == null || pp.isEmpty()) {
            return null;
        }
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        return pvtModel.getToolsList().stream().filter(p -> p.getId().equals(pp.get("id").toString())).findFirst().get();
    }

}
