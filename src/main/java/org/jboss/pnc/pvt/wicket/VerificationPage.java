package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.Verification;
import org.jboss.pnc.pvt.model.VerifyTool;

import java.util.Calendar;

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

        String verifyId = pp.get(0).toString();

        add(new Label("id", verifyId));

        Verification verification = PVTApplication.getDAO().getPvtModel().getVerificationById(verifyId);
        VerifyTool tool = PVTApplication.getDAO().getPvtModel().getVerifyToolById(verification.getToolId());

        add(new Label("tool_name", tool.toString()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(verification.getStartTime());
        add(new Label("startTime", calendar.getTime().toString()));

        add(new Label("status", verification.getStatus()));
    }
}