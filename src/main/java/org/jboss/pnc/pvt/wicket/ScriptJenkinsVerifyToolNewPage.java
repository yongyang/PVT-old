package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.ExecutionVariable;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class ScriptJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public ScriptJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public ScriptJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        final PropertyModel<VerifyTool> configModel = new PropertyModel<VerifyTool>(form.getModelObject(), "jenkinsConfigXML");
        form.add(new MultiLineLabel("jenkinsConfigXML", configModel));
        form.add(new TextArea<String>("script") {

            @Override
            protected void onModelChanged() {
                configModel.setObject(form.getModelObject());
            }
        });
        form.add(new TextField<String>("jobId"));
        form.add(new TextField<String>("archiver"));

        final ScriptJenkinsVerifyTool scriptTool = (ScriptJenkinsVerifyTool) tool;

        List<String> varables = new ArrayList<>();
        varables.addAll(ExecutionVariable.getVariables().keySet());
        final ListView<String> paramsListView = new ListView<String>("stringParams") {

            @Override
            protected void populateItem(final ListItem<String> item) {
                DropDownChoice<String> stringParam = new DropDownChoice<String>("paramName", Model.of(), varables) {

                    @Override
                    public boolean isNullValid() {
                        return false;
                    }

                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }

                    @Override
                    protected void onSelectionChanged(String newParam) {
                        setModelObject(newParam);
                        item.setModelObject(newParam);
                    }

                };

                stringParam.setRequired(true);
                stringParam.setModelObject(item.getModelObject());
                item.add(stringParam);

                final Button removeParamBtn = new Button("removeParamBtn") {
                    @Override
                    public void onSubmit() {
                        scriptTool.getStringParams().remove(stringParam.getModelObject());
                        item.remove();
                    }
                };
                removeParamBtn.setDefaultFormProcessing(false);
                item.add(removeParamBtn);
            }
        };
        form.add(paramsListView);

        // add button
        Button addParamBtn = new Button("addParamBtn"){
            @Override
            public void onSubmit() {
                scriptTool.getStringParams().add(ExecutionVariable.CURRENT_PRODUCT_ID.getName()); // default 
                paramsListView.setModelObject(scriptTool.getStringParams());
            }
        };
        addParamBtn.setDefaultFormProcessing(false);
        form.add(addParamBtn);
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new ScriptJenkinsVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a script jenkins verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new ScriptJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}