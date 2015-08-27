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
public class SimpleJenkinsVerifyToolEditPage extends SimpleJenkinsVerifyToolNewPage {

    private static final long serialVersionUID = 1L;

    public SimpleJenkinsVerifyToolEditPage(PageParameters pp) {
        this(pp, null);
    }

    public SimpleJenkinsVerifyToolEditPage(PageParameters pp, String info) {
        super(pp, info);
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return searchVerifyTool(pp);
    }

    @Override
    protected String getTitle() {
        return "Edit simple jenkins verify tool";
    }

    @Override
    protected void onConfigure() {
        removeButton.setVisible(true);
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        //TODO nothing to do with DB yet
        setResponsePage(new SimpleJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is updated."));
    }
}
