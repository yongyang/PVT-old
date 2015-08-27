package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.TemplateJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
public class TemplateJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        form.add(new TextArea<String>("jenkinsConfigXML"));
        form.add(new TextField<String>("jobId"));
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
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new TemplateJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}
