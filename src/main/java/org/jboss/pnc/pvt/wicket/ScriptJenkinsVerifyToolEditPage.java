package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.VerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
public class ScriptJenkinsVerifyToolEditPage extends ScriptJenkinsVerifyToolNewPage {

    private static final long serialVersionUID = 1L;

    public ScriptJenkinsVerifyToolEditPage(PageParameters pp) {
        this(pp, null);
    }

    public ScriptJenkinsVerifyToolEditPage(PageParameters pp, String info) {
        super(pp, info);
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return searchVerifyTool(pp);
    }

    @Override
    protected String getTitle() {
        return "Edit script jenkins verify tool";
    }

    @Override
    protected void onConfigure() {
        removeButton.setVisible(true);
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        //TODO nothing to do with DB yet
        setResponsePage(new ScriptJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is updated."));
    }
}
