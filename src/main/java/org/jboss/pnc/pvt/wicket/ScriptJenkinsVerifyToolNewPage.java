package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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

        ScriptJenkinsVerifyTool scriptTool = (ScriptJenkinsVerifyTool) tool;

        List<ExecutionVariable> varables = new ArrayList<>();
        varables.addAll(ExecutionVariable.getVariables().values());

        final MarkupContainer rowPanel = new WebMarkupContainer("rowPanel");
        rowPanel.setOutputMarkupId(true);
        form.add(rowPanel);

        final ListView<String> paramsListView = new ListView<String>("stringParams") {

            @Override
            protected void populateItem(final ListItem<String> item) {

                ListView<String> theListView = this;
                final DropDownChoice<ExecutionVariable> stringParam = new DropDownChoice<ExecutionVariable>("paramName",
                        Model.of(), varables, new IChoiceRenderer<ExecutionVariable>() {
                            @Override
                            public Object getDisplayValue(ExecutionVariable var) {
                                return var.getName();
                            }

                            @Override
                            public String getIdValue(ExecutionVariable var, int index) {
                                return var.getName();
                            }
                        }) {

                    @Override
                    public boolean isNullValid() {
                        return false;
                    }

                    @Override
                    protected void onModelChanged() {
                        String newV = getModelObject().getName();
                        item.setModelObject(newV);
                        item.modelChanged();
                        theListView.modelChanged();
                    }

                };
                item.add(stringParam);

                stringParam.setRequired(true);
                stringParam.setModelObject(ExecutionVariable.getVariables().get(item.getModelObject()));
                AjaxLink<String> removeParamLink = new AjaxLink<String>("removeParamLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getList().remove(item.getIndex()); // removed data from list
                        if (target != null)
                            target.add(rowPanel);
                    }

                };
                item.add(removeParamLink);

            }

        };
        paramsListView.setReuseItems(true);
        paramsListView.setOutputMarkupId(true);
        rowPanel.add(paramsListView);

        AjaxLink<String> addParamLink = new AjaxLink<String>("addParamLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                paramsListView.getModelObject().add(ExecutionVariable.CURRENT_PRODUCT_ID.getName()); // default
                if (target != null)
                    target.add(rowPanel);
            }

        };
        form.add(addParamLink);
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