package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.*;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
public class JDKCompatibleVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public JDKCompatibleVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public JDKCompatibleVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        form.add(new RequiredTextField<String>("expectJDKVersion"));
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new JDKCompatibleVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a new JDK compatible verify tool";
    }

    @Override
    protected void doSubmit() {
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
    }
}
