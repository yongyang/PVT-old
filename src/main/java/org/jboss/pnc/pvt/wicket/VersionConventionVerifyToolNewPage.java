package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.model.VersionConventionVerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class VersionConventionVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public VersionConventionVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public VersionConventionVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        form.add(new TextField<String>("versionPattern", new PropertyModel<String>(tool, "versionPattern")));
        form.add(new CheckBox("checkOSGI", new PropertyModel<Boolean>(tool, "checkOSGI")));
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new VersionConventionVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a new version convention verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new VersionConventionVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}
