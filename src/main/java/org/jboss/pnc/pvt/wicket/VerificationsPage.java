package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.Verification;
import org.jboss.pnc.pvt.model.VerifyTool;

import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class VerificationsPage extends TemplatePage{

    public VerificationsPage(PageParameters pp) {
        this(pp,"PVT products loaded.");
    }

    public VerificationsPage(PageParameters pp, String info) {
    	super(pp, info);
        setActiveMenu(Menu.VERIFICATIONS);

        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        List<Verification> verifications = pvtModel.getVerificationsList();

        add(new Label("verification_count", Model.of("" + verifications.size())));

        add(new ListView<Verification>("verification_rows", verifications) {
            @Override
            protected void populateItem(ListItem<Verification> item) {
            	Link verification_link = new Link("verification_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set("verificationId", item.getModel().getObject().getId());
                        setResponsePage(VerificationPage.class, pp);
                    }
                };
                Release release = PVTApplication.getDAO().getPvtModel().getReleasebyId(item.getModel().getObject().getReleaseId());
                Product product = PVTApplication.getDAO().getPvtModel().getProductById(release.getProductId());
                verification_link.add(new Label("product_name", product.getName()));
                item.add(verification_link);
                item.add(new Label("release_name", release.getName()));
                
                VerifyTool tool = PVTApplication.getDAO().getPvtModel().getVerifyToolById(item.getModel().getObject().getToolId());
                item.add(new Label("tool_name", tool.getName()));
                
                item.add(new Label("verification_status", item.getModel().getObject().getStatus().name()));
                
                if(item.getModel().getObject().getReferenceReleaseId() != null ){
                	item.add(new Label("valid", "Valid"));
                }else
                	item.add(new Label("valid", "Invalid"));
                
                if(verifications.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }

            }
        });

    }

}


