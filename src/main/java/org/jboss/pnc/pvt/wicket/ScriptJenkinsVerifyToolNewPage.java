package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
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
