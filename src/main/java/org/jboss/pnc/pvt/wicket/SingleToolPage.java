package org.jboss.pnc.pvt.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool.Level;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool.Type;

import com.googlecode.wicket.kendo.ui.form.TextArea;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
public class SingleToolPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private static int MODE_EDIT = 1;

    // view mode is not used for now.
    private static int MODE_VIEW = 2;

    private static int MODE_CREATE = 3;

    /**
     * page mode, default to MODE_EDIT
     */
    private int mode = MODE_EDIT;

    private FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");

    public SingleToolPage(PageParameters pp) {
        this(pp, null);
    }

    public SingleToolPage(PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu("tools");

        add(feedBackPanel);

        if (pp != null) {
            StringValue modeStr = pp.get("mode");
            if (!modeStr.isNull()) {
                try {
                    mode = modeStr.toInt();
                } catch (StringValueConversionException e) {
                    warn("Bad parameter: mode: " + modeStr.toString());
                    mode = MODE_EDIT;
                }
            }
        }

        final ScriptJenkinsVerifyTool tool;
        if (mode == MODE_EDIT || mode == MODE_VIEW) {
            add(new Label("tool_summary", "Edit Verification Tool"));
            tool = getToolByIdOrName(pp);
            if (tool == null) {
                throw new IllegalStateException("Tool is null in edit mode");
            }
        } else if (mode == MODE_CREATE) {
            add(new Label("tool_summary", "Create a Verification Tool"));
            tool = new ScriptJenkinsVerifyTool();
        } else {
            throw new IllegalArgumentException("Wrong mode parameter: mode = " + mode);
        }

        assert tool != null : "Tool should not be null from this point";

        final Form<ScriptJenkinsVerifyTool> toolForm = new Form<ScriptJenkinsVerifyTool>("form-tool", new CompoundPropertyModel<ScriptJenkinsVerifyTool>(tool)) {

            @Override
            protected void onSubmit() {
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                    updateTool(getModelObject());
                    setInfo("Tool: " + tool.getName() + " is Updated.");
                } else if (mode == MODE_CREATE) {
                    createTool(getModelObject());
                    PageParameters pp = new PageParameters();
                    pp.set("mode", MODE_EDIT + "");
                    pp.set("name", getModelObject().getName());
                    setResponsePage(new SingleToolPage(pp, "Tool: " + getModelObject().getName() + " is created."));
                } else {
                    throw new IllegalArgumentException("Wrong mode parameter: mode = " + mode);
                }
            }
        };

        Button backButton = new Button("back") {

            @Override
            public void onSubmit() {
                PageParameters pp = new PageParameters();
                setResponsePage(ListToolsPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);

        toolForm.add(backButton);

        Button resetButton = new Button("reset") {

            @Override
            public void onSubmit() {
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                    toolForm.getModel().setObject(getToolByIdOrName(pp));
                } else {
                    toolForm.getModel().setObject(new ScriptJenkinsVerifyTool());
                }
            }
        };
        toolForm.add(resetButton);

        Button removeButton = new Button("remove") {

            @Override
            public void onSubmit() {
                removeTool(toolForm.getModelObject());
                PageParameters pp = new PageParameters();
                setResponsePage(new ListToolsPage(pp, "Tool: " + tool.getName() + " is removed."));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(true);
                setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
            }

            @Override
            public boolean isVisible() {
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                    return true;
                }
                return false;
            }
        };
        toolForm.add(removeButton);
        removeButton.setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
        toolForm.add(new RequiredTextField<String>("name"));

        IModel<Type> defaultType = Model.of(tool.getType() == null ? Type.STATIC : tool.getType());
        toolForm.getModelObject().setType(defaultType.getObject());
        List<Type> types = Arrays.asList(Type.values());
        DropDownChoice<Type> toolTypeChoice = new DropDownChoice<Type>("type", defaultType, Model.ofList(types)) {
            @Override
            protected void onModelChanged() {
                toolForm.getModelObject().setType(getModelObject());
            }
        };
        toolTypeChoice.setRequired(true);
        toolForm.add(toolTypeChoice);

        IModel<Level> defaultLevel = Model.of(tool.getLevel() == null ? Level.ALL : tool.getLevel());
        toolForm.getModelObject().setLevel(defaultLevel.getObject());
        List<Level> levels = Arrays.asList(Level.values());
        DropDownChoice<Level> toolLevelChoice = new DropDownChoice<Level>("level", defaultLevel, Model.ofList(levels)) {

            @Override
            protected void onModelChanged() {
                toolForm.getModelObject().setLevel(getModelObject());
            }
        };
        toolLevelChoice.setRequired(true);
        toolForm.add(toolLevelChoice);

        toolForm.add(new TextArea<String>("description"));
        toolForm.add(new TextArea<String>("command"));

        add(toolForm);
    }

    private ScriptJenkinsVerifyTool getToolByIdOrName(PageParameters pp) {
        assert pp != null : "Wrong state!!";
        StringValue str = pp.get("id");
        if (str.isNull() || str.isEmpty()) {
            str = pp.get("name");
        }
        if (str.isNull() || str.isEmpty()) {
            return null;
        }
        for (ScriptJenkinsVerifyTool t: ListToolsPage.getAllTools()) {
            if (str.toString().equals(t.getId() + "")
                    || str.toString().equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    private void createTool(ScriptJenkinsVerifyTool tool) {
        ListToolsPage.getAllTools().add(tool);
    }

    private void updateTool(ScriptJenkinsVerifyTool tool) {
        //TODO update db
    }

    private void removeTool(ScriptJenkinsVerifyTool tool) {
        ListToolsPage.getAllTools().remove(tool);
    }
}
