package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.VerifyTool;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public abstract class AbstractVerifyToolPage extends TemplatePage {

    protected Form<VerifyTool> form;

    protected Button resetButton;
    protected Button removeButton;

    protected VerifyTool tool;

    public AbstractVerifyToolPage(PageParameters pp) {
        this(pp, null);
    }

    public AbstractVerifyToolPage(PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu("tools");

        add(new FeedbackPanel("feedbackMessage"));

        add(new Label("tool_summary", getTitle()));

        tool = getVerifyTool(pp);

        form = new Form<VerifyTool>("form-tool"){
            @Override
            protected void onSubmit() {
                doSubmit();
                setResponsePage(new ToolsPage(pp, "Tool: " + getModelObject().getName() + " is created."));
            }

        };

        CompoundPropertyModel<VerifyTool> toolModel = new CompoundPropertyModel<VerifyTool>(tool);
        form.setModel(toolModel);

        // adds common fields
        form.add(new RequiredTextField<String>("name"));

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
                doReset();
            }
        };

        form.add(resetButton);

        removeButton = new Button("remove") {

            @Override
            public void onSubmit() {
                doRemove(form.getModelObject());
                PageParameters pp = new PageParameters();
                setResponsePage(new ToolsPage(pp, "Tool: " + form.getModelObject().getName() + " is removed."));
            }

        };
//        removeButton.setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
        form.add(removeButton);

        add(form);
    }

    @Override
    protected void onConfigure() {
        removeButton.setVisible(false);
    }

    protected abstract VerifyTool getVerifyTool(PageParameters pp);

    protected abstract void doSubmit();

    protected void doReset() {
        form.getModel().setObject(getVerifyTool(null));
    }

    protected void doRemove(VerifyTool tool) {
        DataProvider.getAllTools().remove(tool);
    }

    protected String getTitle() {
        return "Define A JDK compatible verify tool";
    }
}
