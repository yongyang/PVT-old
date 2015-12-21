package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.jboss.pnc.pvt.model.*;

import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ReleasesPage extends TemplatePage{

    public ReleasesPage(PageParameters pp) {
        this(pp, "PVT releases loaded.");
    }

    public ReleasesPage(PageParameters pp,String info) {
        super(pp,info);
        setActiveMenu(Menu.RELEASES);

        add(new Link<String>("link-newrelease") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set("mode", "3"); // MODE_CREATE
                setResponsePage(ReleaseNewPage.class, pp);
            }
        });

        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        List<Release> releases = pvtModel.getReleases();
        add(new Label("releases_count", Model.of("" + releases.size())));

        add(new ListView<Release>("release_rows", releases) {
            @Override
            protected void populateItem(ListItem<Release> item) {
                final Release release = item.getModelObject();
                Link product_link = new Link("product_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set("productId", item.getModel().getObject().getProductId());
                        setResponsePage(ProductEditPage.class, pp);
                    }
                };
                String productId = item.getModel().getObject().getProductId();
                String productName = PVTApplication.getDAO().getPvtModel().getProductById(productId).getName();
                product_link.add(new Label("product_name", productName));
                item.add(product_link);

                item.add(new Link<String>("release_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set("releaseId", item.getModel().getObject().getId());
                        setResponsePage(ReleaseEditPage.class, pp);
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "name");
                    }
                });

                Label releaseStatusLabel = new Label("release_status", new PropertyModel(item.getModel(), "status"));
                releaseStatusLabel.add(new AbstractAjaxTimerBehavior(Duration.seconds(10L)) {
                    @Override
                    protected void onTimer(AjaxRequestTarget target) {
                        if (release.getStatus() != release.updateStatus()){
                            PVTApplication.getDAO().persist();
                        }
                        target.add(releaseStatusLabel);
                        if (releaseStatusLabel.getDefaultModel().getObject().equals(Release.Status.REJECTED) ||
                                releaseStatusLabel.getDefaultModel().getObject().equals(Release.Status.PASSED)) {
                            stop(target);
                        }
                    }
                });

                item.add(releaseStatusLabel);


                item.add(new Label("release_description", new PropertyModel(item.getModel(), "description")));
                item.add(new ListView<String>("release_tools", item.getModelObject().getTools()) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        String toolId = item.getModelObject();
                        Link<String> toolLink = new Link<String>("release_tool", Model.of(pvtModel.getVerifyToolById(toolId).getName())) {
                            @Override
                            public void onClick() {
                                VerifyTool tool = pvtModel.getVerifyToolById(toolId);
                                PageParameters pp = new PageParameters();
                                pp.add("id", tool.getId());
                                String newPageClassName = ToolsPage.class.getPackage().getName() + tool.getClass().getName().substring(tool.getClass().getName().lastIndexOf(".")) + "EditPage";
                                try {
                                    Class newPageClass = getClass().getClassLoader().loadClass(newPageClassName);
                                    setResponsePage(newPageClass, pp);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public IModel<?> getBody() {
                                return Model.of(pvtModel.getVerifyToolById(toolId).getName());
                            }
                        };

                        item.add(toolLink);

                        String verificationId = release.getVerificationIdByToolId(toolId);

                        Link<String> verificationLink = new Link<String>("verification-link") {
                            @Override
                            public void onClick() {
                                PageParameters pageParameters = new PageParameters();
                                pageParameters.set("verificationId", verificationId);
                                setResponsePage(VerificationPage.class, pageParameters);
                            }
                        };
                        Label verificationStatus = new Label(
                                "release_verification_status",
                                new Model<Verification.Status>() {
                                    @Override
                                    public Verification.Status getObject() {
                                        String verificationId = release.getVerificationIdByToolId(toolId);
                                        return (verificationId != null && pvtModel.getVerificationById(verificationId) != null) ? pvtModel.getVerificationById(verificationId).getStatus() : Verification.Status.NEW;
                                    }
                                }
                        );
                        if (verificationStatus.getDefaultModel().getObject().equals(Verification.Status.NEW) ||
                                verificationStatus.getDefaultModel().getObject().equals(Verification.Status.IN_PROGRESS)) {

                            verificationStatus.add(new AbstractAjaxTimerBehavior(Duration.seconds(5L)) {
                                @Override
                                protected void onTimer(AjaxRequestTarget target) {
                                    target.add(verificationStatus);
                                    if (verificationStatus.getDefaultModel().getObject().equals(Verification.Status.NOT_PASSED) ||
                                            verificationStatus.getDefaultModel().getObject().equals(Verification.Status.PASSED)) {
                                        stop(target);
                                    }
                                }
                            });
                        }
                        item.add(verificationLink);
                        verificationLink.add(verificationStatus);
                        if (verificationId == null) {
                            // Hide verification if can not get verificationId
                            verificationLink.setVisible(false);
                        }
                    }
                });
                item.add(new ListView<String>("release_distributions", Arrays.asList(item.getModelObject().getDistributionArray())) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        ExternalLink link = new ExternalLink("release_distribution", item.getModelObject()) {
                            @Override
                            public IModel<?> getBody() {
                                return Model.of(item.getModelObject());
                            }

                        };
//                        link.add(AttributeModifier.append("target", "_blank")); // target set in html
                        link.add(AttributeModifier.append("title", item.getModelObject()));
                        item.add(link);
                    }
                });
                item.add(new Label("release_createTime", new Date(item.getModel().getObject().getCreateTime()).toString()));
                if (releases.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }

                AjaxLink<String> verifyLink = new AjaxLink<String>("release_verify") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        verifyRelease(item.getModelObject());
                        // reload Release List page
                        setResponsePage(ReleasesPage.class);
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        AjaxCallListener ajaxCallListener = new AjaxCallListener();
                        if(release.getTools().isEmpty()){
                            ajaxCallListener.onPrecondition("alert('No tool configed!'); return false;");
                        }
                        else {
                            ajaxCallListener.onPrecondition("return confirm('" + "Start to verify release: " + item.getModelObject().getName() + "?');");
                        }
                        attributes.getAjaxCallListeners().add(ajaxCallListener);
                    }
                };
                item.add(verifyLink);

                Link<String> releaseLink = new Link<String>("release_view") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set("releaseId", item.getModel().getObject().getId());
                        setResponsePage(ReleaseEditPage.class, pp);
                    }
                };
                item.add(releaseLink);

            }
        });
    }

    public static void verifyRelease(Release release) {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();

//        Collection<String> existedVerifications = release.getVerifications();

        for(Map.Entry<String, String> entry : release.getToolsMap().entrySet()) {
            if(entry.getValue() == null) { // not run yet
               runVerify(entry.getKey(), release, null);

            }
            else { // has run before, detect if the Tool need to run again
                String verificationId = entry.getValue();
                Verification existedVerification = pvtModel.getVerificationById(verificationId);
                if(existedVerification == null || release.getUpdateTime() > existedVerification.getStartTime() || existedVerification.getStatus().equals(Verification.Status.NOT_PASSED)) { //need to run again
//                    existedVerifications.remove(toolId);
                    runVerify(entry.getKey(), release, null);
                }
            }
        }
        // persist all verifications
        PVTApplication.getDAO().persist();
    }

    public static Verification runVerify(String toolId, Release release, Properties verifyProps){
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        VerifyTool tool = pvtModel.getVerifyToolById(toolId);
        // start verification and link to Release
        VerifyParameter verifyParam = new VerifyParameter(tool.getId(),
                (release.getReferenceReleaseId() == null || release.getReferenceReleaseId().trim().isEmpty()) ? null : pvtModel.getReleasebyId(release.getReferenceReleaseId()),
                release, verifyProps);
        Verification verification = tool.verify(verifyParam);
//        verification.setStatus(Verification.Status.IN_PROGRESS);
        release.addVerification(verification.getToolId(), verification.getId());
        release.setStatus(Release.Status.VERIFYING);
        pvtModel.addVerification(verification);
        pvtModel.updateRelease(release);
//      pvtModel.addVerification(verification);
        return verification;
    }

}


