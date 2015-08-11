package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class Page404 extends TemplatePage {

    private static final long serialVersionUID = 579502096694178470L;

    public Page404(PageParameters pp) {
        this(pp, null);
    }
    
    public Page404(PageParameters pp, String info) {
    	super(pp, info);
    	add(new FeedbackPanel("feedbackMessage"));
    }
}
