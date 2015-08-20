package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.JDKCompatibleVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;


/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial"})
public class JDKCompatibleVerifyToolEditPage extends JDKCompatibleVerifyToolNewPage {

    private static final long serialVersionUID = 1L;

    public JDKCompatibleVerifyToolEditPage(PageParameters pp) {
        this(pp, null);
    }

    public JDKCompatibleVerifyToolEditPage(PageParameters pp, String info) {
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
}
