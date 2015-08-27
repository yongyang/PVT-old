package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerificationPage extends TemplatePage {
    public VerificationPage(PageParameters pp) {
        this(pp, "PVT verification loaded.");
    }

    public VerificationPage(PageParameters pp, String info) {
        super(pp, info);

        setActiveMenu(Menu.VERIFICATIONS);

    }
}