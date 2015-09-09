package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.Product;
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
        Verification verification = PVTApplication.getDAO().getPvtModel().getVerificationById(verifyId);
        VerifyTool tool = PVTApplication.getDAO().getPvtModel().getVerifyToolById(verification.getToolId());
        Release release = PVTApplication.getDAO().getPvtModel().getReleasebyId(verification.getReleaseId());
        Product product = PVTApplication.getDAO().getPvtModel().getProductById(release.getProductId());
//        Release refRelease = PVTApplication.getDAO().getPvtModel().getReleasebyId(verification.getReferenceReleaseId());

        add(new Label("id", verifyId));


        add(new Label("tool_name", tool.toString()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(verification.getStartTime());
        add(new Label("startTime", calendar.getTime().toString()));

        long endTime = verification.getEndTime();
        Calendar endCalendar = Calendar.getInstance();
        if(endTime != 0) {
            calendar.setTimeInMillis(endTime);
        }

        add(new Label("endTime", endTime == 0 ? " - "  : endCalendar.getTime().toString()));

        add(new Label("status", verification.getStatus()));
        add(new Label("release", product.getName() + " " + release.getName()));

        if(verification.getExecution().getLink() != null) {
            add(new ExternalLink("execution_link", verification.getExecution().getLink(), verification.getExecution().getLink()));
        }
        else {
            add(new ExternalLink("execution_link", "#", verification.getExecution().getLink()));
        }

        if(verification.getExecution().getReport().getMainLog() != null) {
            add(new Label("execution_log", verification.getExecution().getReport().getMainLog()));
        }
        else {
            add(new Label("execution_log", ""));
        }

        if(verification.getExecution().getException() != null) {
            add(new Label("execution_exception", verification.getExecution().getException()));
        }
        else {
            add(new Label("execution_exception", ""));
        }
    }
}