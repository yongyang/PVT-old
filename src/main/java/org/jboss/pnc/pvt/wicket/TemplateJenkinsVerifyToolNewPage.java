package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
    protected void doSubmit() {

    }
}
