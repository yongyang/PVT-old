package org.jboss.pnc.pvt.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.jboss.pnc.pvt.model.JDKCompatibleVerifyTool;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.SimpleJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.TemplateJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.model.VersionConventionVerifyTool;

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

    public static final int MODE_EDIT = 1;

    // view mode is not used for now.
    public static final int MODE_VIEW = 2;

    public static final int MODE_CREATE = 3;

    /**
     * page mode, default to MODE_EDIT
     */
    private int mode = MODE_CREATE;

    private final String variant;

    public SingleToolPage(PageParameters pp) {
        this(pp, null);
    }

    public SingleToolPage(PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu("tools");

        add(new FeedbackPanel("feedbackMessage"));

        if (pp != null) {
            StringValue modeStr = pp.get("mode");
            if (!modeStr.isNull()) {
                try {
                    mode = modeStr.toInt();
                } catch (StringValueConversionException e) {
                    mode = MODE_CREATE;
                }
            }
        }

        final VerifyTool tool = initializeTool(pp);
        if (tool == null) {
            redirectToInterceptPage(new Page404(pp));
        }
        this.variant = "tool.getPageVariant()";
        add(createToolForm(tool, pp));
    }

    @Override
    public String getVariation() {
        return this.variant;
    }

    /**
     * Initialize the VerifyTool this page works on.
     * In MODE_EDIT or MODE_VIEW, it will load the information according to the parameters in pp
     * In MODE_CREATE, it will create a VerifyTool instance based on typeLabel
     */
    private VerifyTool initializeTool(PageParameters pp) {
        if (mode == MODE_EDIT || mode == MODE_VIEW) {
            add(new Label("tool_summary", "Edit Verification Tool"));
            return getToolByIdOrName(pp);
        } else if (mode == MODE_CREATE) {
            add(new Label("tool_summary", "Create a Verification Tool"));
            return createVerifyTool(pp);
        }
        // return null which will lead to 404 page
        return null;
    }

    /**
     * Gets tool by it's id or name
     * 
     * @param pp page parameters
     * @return the tool retrieved from db or null if not found
     */
    private VerifyTool getToolByIdOrName(PageParameters pp) {
        assert pp != null : "Wrong state!!";
        StringValue str = pp.get("id");
        if (str.isNull() || str.isEmpty()) {
            str = pp.get("name");
        }
        if (str.isNull() || str.isEmpty()) {
            return null;
        }
        for (VerifyTool t: getAllTools()) {
            if (str.toString().equals(t.getId() + "")
                    || str.toString().equals(t.getName())) {
                return t;
            }
        }
        return null;
    }

    /**
     * This will create a VerifyTool instance according to the 'label' in pp in MODE_CREATE.
     */
    private VerifyTool createVerifyTool(PageParameters pp) {
        String label = null;
        if (pp != null) {
            StringValue labelStr = pp.get("label");
            if (!labelStr.isNull() && !labelStr.isEmpty()) {
                label = labelStr.toString();
            }
        }
        if (label == null) {
            return null;
        }
        return VerifyTool.createVerifyTool(label);
    }

    private abstract class AbstractToolForm extends Form<VerifyTool> {

        protected final PageParameters pp;

        private AbstractToolForm(final VerifyTool tool, final PageParameters pp) {
            super("form-tool");
            this.pp = pp;
            CompoundPropertyModel<VerifyTool> toolModel = new CompoundPropertyModel<VerifyTool>(tool);
            this.setModel(toolModel);
            addsComponents();
        }

        @Override
        protected void onSubmit() {
            if (mode == MODE_EDIT || mode == MODE_VIEW) {
                updateTool(getModelObject());
                setInfo("Tool: " + getModelObject().getName() + " is Updated.");
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

        /**
         * All sub classes should call super.addsComponents() to add common fields
         */
        protected void addsComponents() {
            // adds common fields
            add(new RequiredTextField<String>("name"));

            add(new TextArea<String>("description"));

            // adds buttons
            Button backButton = new Button("back") {

                @Override
                public void onSubmit() {
                    PageParameters pp = new PageParameters();
                    setResponsePage(ToolsPage.class, pp);
                }
            };
            backButton.setDefaultFormProcessing(false);
            add(backButton);

            Button resetButton = new Button("reset") {

                @Override
                public void onSubmit() {
                    AbstractToolForm.this.getModel().setObject(initializeTool(pp));
                }
            };
            add(resetButton);

            Button removeButton = new Button("remove") {

                @Override
                public void onSubmit() {
                    removeTool(AbstractToolForm.this.getModelObject());
                    PageParameters pp = new PageParameters();
                    setResponsePage(new ToolsPage(pp, "Tool: " + AbstractToolForm.this.getModelObject().getName() + " is removed."));
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
            removeButton.setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
            add(removeButton);
        }
    }
    
    private class JDKCompatibleToolForm extends AbstractToolForm {
        public JDKCompatibleToolForm(VerifyTool tool, PageParameters pp) {
            super(tool, pp);
        }

        @Override
        protected void addsComponents() {
            super.addsComponents(); // call super first
            add(new RequiredTextField<String>("expectJDKVersion"));
        }
    }

    private class VersionConventionToolForm extends AbstractToolForm {
        public VersionConventionToolForm(VerifyTool tool, PageParameters pp) {
            super(tool, pp);
        }

        @Override
        protected void addsComponents() {
            super.addsComponents(); // call super first
        }
    }

    private class SimpleJenkinsToolForm extends AbstractToolForm {
        public SimpleJenkinsToolForm(VerifyTool tool, PageParameters pp) {
            super(tool, pp);
        }

        @Override
        protected void addsComponents() {
            super.addsComponents(); // call super first
            add(new RequiredTextField<String>("jobId"));
        }
    }

    private class TemplateJenkinsToolForm extends AbstractToolForm {
        public TemplateJenkinsToolForm(VerifyTool tool, PageParameters pp) {
            super(tool, pp);
        }

        @Override
        protected void addsComponents() {
            super.addsComponents(); // call super first
            add(new TextArea<String>("jenkinsConfigXML"));
        }
    }

    private class ScriptJenkinsToolForm extends AbstractToolForm {
        public ScriptJenkinsToolForm(VerifyTool tool, PageParameters pp) {
            super(tool, pp);
        }

        @Override
        protected void addsComponents() {
            super.addsComponents(); // call super first
            final PropertyModel<VerifyTool> configModel = new PropertyModel<VerifyTool>(getModelObject(), "jenkinsConfigXML");
            add(new MultiLineLabel("jenkinsConfigXML", configModel));
            add(new TextArea<String>("script") {

                @Override
                protected void onModelChanged() {
                    configModel.setObject(ScriptJenkinsToolForm.this.getModelObject());
                }
            });
        }
    }

    /**
     * Populate the Tool Form according to different VerifyTool type.
     * 
     * @param tool the VerifyTool
     * @param pp the page parameter
     * @return the Tool form used to edit/create the Tool.
     */
    private AbstractToolForm createToolForm(VerifyTool tool, PageParameters pp) {
        if (tool instanceof JDKCompatibleVerifyTool) {
            return new JDKCompatibleToolForm(tool, pp);
        } else if (tool instanceof VersionConventionVerifyTool) {
            return new VersionConventionToolForm(tool, pp);
        } else if (tool instanceof SimpleJenkinsVerifyTool) {
            return new SimpleJenkinsToolForm(tool, pp);
        } else if (tool instanceof ScriptJenkinsVerifyTool) {
            return new ScriptJenkinsToolForm(tool, pp);
        } else if (tool instanceof TemplateJenkinsVerifyTool) {
            return new TemplateJenkinsToolForm(tool, pp);
        }
        throw new IllegalStateException("Not supported tool type: " + tool.getLabel());
    }

    // =======================================
    // Followings are DB related methods
    // =======================================

    private List<VerifyTool> getAllTools() {
        return DataProvider.getAllTools();
    }

    private void createTool(VerifyTool tool) {
        DataProvider.getAllTools().add(tool);
    }

    private void updateTool(VerifyTool tool) {
        ;
    }

    private void removeTool(VerifyTool tool) {
        DataProvider.getAllTools().remove(tool);
    }
}
