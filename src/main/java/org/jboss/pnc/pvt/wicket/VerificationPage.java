package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
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
        this(pp, "PVT verifications loaded.");
    }

    public VerificationPage(PageParameters pp, String info) {
        super(pp, info);

        setActiveMenu(Menu.VERIFICATIONS);

        String verifyId = pp.get("verificationId").toString();
        Verification verification = PVTApplication.getDAO().getPvtModel().getVerificationById(verifyId);

        VerifyTool tool = PVTApplication.getDAO().getPvtModel().getVerifyToolById(verification.getToolId());
        Release release = PVTApplication.getDAO().getPvtModel().getReleasebyId(verification.getReleaseId());
        Product product = PVTApplication.getDAO().getPvtModel().getProductById(release.getProductId());
//        Release refRelease = PVTApplication.getDAO().getPvtModel().getReleasebyId(verification.getReferenceReleaseId());

        Form verificationForm = new Form("verification_form", new CompoundPropertyModel(verification));
        add(verificationForm);

        verificationForm.add(new Label("id", verifyId));
        verificationForm.add(new Label("tool_name", tool.getName()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(verification.getStartTime());
        verificationForm.add(new Label("startTime", calendar.getTime().toString()));

        long endTime = verification.getEndTime();
        Calendar endCalendar = Calendar.getInstance();
        if(endTime != 0) {
            calendar.setTimeInMillis(endTime);
        }
        verificationForm.add(new Label("endTime", endTime == 0 ? " - " : endCalendar.getTime().toString()));

        verificationForm.add(new Label("status", verification.getStatus()));
        verificationForm.add(new Label("release", product.getName() + " " + release.getName()));

        if(verification.getExecution().getLink() != null) {
        	verificationForm.add(new ExternalLink("execution_link", verification.getExecution().getLink(), verification.getExecution().getLink()));
        }
        else {
        	verificationForm.add(new ExternalLink("execution_link", "#", verification.getExecution().getLink()));
        }

        if(verification.getExecution().getReport().getMainLog() != null) {
        	verificationForm.add(new Label("execution_log", verification.getExecution().getReport().getMainLog()));
        }
        else {
        	verificationForm.add(new Label("execution_log", ""));
        }

        if(verification.getExecution().getException() != null) {
        	verificationForm.add(new Label("execution_exception", verification.getExecution().getException()));
        }
        else {
        	verificationForm.add(new Label("execution_exception", ""));
        }

        WebMarkupContainer waiveTR = new WebMarkupContainer("waive_tr");
        verificationForm.add(waiveTR);
        TextArea<String> waiveComment = new TextArea<String>("waiveComment");
        waiveTR.add(waiveComment);

        Button waiveButton = new Button("waive_button", Model.of("Waive")) {
            @Override
            public void onSubmit() {
                super.onSubmit();
                verification.setWaiveComment(waiveComment.getValue());
                verification.setStatus(Verification.Status.WAIVED);
                PVTApplication.getDAO().getPvtModel().updateVerification(verification);
                PVTApplication.getDAO().persist();
                setResponsePage(VerificationPage.class, new PageParameters().set(0, verification.getId())); // reload
            }

        };
        verificationForm.add(waiveButton);


        verificationForm.add(new Button("cancel_button") {
            @Override
            public void onSubmit() {
                //STOP this verification, for example: to cancel a unlinked verification
                String toolId = verification.getToolId();
                release.getToolsMap().put(toolId, null);
                PVTApplication.getDAO().persist();
                setResponsePage(ReleasesPage.class);
//                super.onSubmit();
            }
        }.setDefaultFormProcessing(false));


        if(verification.needWaive()) {
            waiveTR.setVisible(false);
            waiveButton.setVisible(false);
        }
    }
}