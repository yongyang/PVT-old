package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.VerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class JDKCompatibleVerifyToolEditPage extends JDKCompatibleVerifyToolNewPage {

    private static final long serialVersionUID = 1L;

    public JDKCompatibleVerifyToolEditPage(PageParameters pp) {
        this(pp, null);
    }

    public JDKCompatibleVerifyToolEditPage(PageParameters pp, String info) {
        super(pp, info);
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return searchVerifyTool(pp);
    }

    @Override
    protected String getTitle() {
        return "Edit JDK compatible verify tool";
    }

    @Override
    protected void onConfigure() {
        removeButton.setVisible(true);
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        //TODO nothing to do with DB yet
        setResponsePage(new JDKCompatibleVerifyToolEditPage(pp, "Tool: " + tool.getName() + " is updated."));
    }
}
