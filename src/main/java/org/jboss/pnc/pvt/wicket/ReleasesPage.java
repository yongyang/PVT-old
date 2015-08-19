package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.Release;

import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ReleasesPage extends TemplatePage{

    public ReleasesPage(PageParameters pp) {
        this(pp,"PVT releases loaded.");
    }

    public ReleasesPage(PageParameters pp,String info) {
        super(pp,info);
        setActiveMenu("releases");

        add(new Link<String>("link-newrelease") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set("mode", "3"); // MODE_CREATE
                setResponsePage(NewReleasePage.class, pp);
            }
        });

        List<Release> releases = ((PVTApplication) Application.get()).getDAO().getPvtModel().getReleases();
        add(new Label("releases_count", Model.of("" + releases.size())));

        add(new ListView<Release>("release_rows", releases) {
            @Override
            protected void populateItem(ListItem<Release> item) {
            	Link product_link = new Link("product_link") {
                    @Override
                    public void onClick() {
                    	PageParameters pp = new PageParameters();
                        pp.set("productId", item.getModel().getObject().getProductId());
                        setResponsePage(EditProductPage.class,pp);
                    }
                };
                String productId = item.getModel().getObject().getProductId();
                String productName = ((PVTApplication) Application.get()).getDAO().getPvtModel().getProductbyId(productId).getName();
                product_link.add(new Label("product_name", productName));
            	item.add(product_link);

                item.add(new Link<String>("release_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set("releaseId", item.getModel().getObject().getId());
                        pp.set("mode", "1");
                        setResponsePage(NewReleasePage.class,pp);
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "name");
                    }
                });


                item.add(new Label("release_status", new PropertyModel(item.getModel(), "status")));
                item.add(new Label("release_description", new PropertyModel(item.getModel(), "description")));
                item.add(new ListView<String>("release_jobs", item.getModelObject().getJobs()) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        Link<String> jobLink = new Link<String>("release_job", Model.of(item.getModelObject())) {
                            @Override
                            public void onClick() {
                                //TODO: open job
                            }

                            @Override
                            public IModel<?> getBody() {
                                return  Model.of(item.getModelObject());
                            }
                        };
                        item.add(jobLink);
                    }
                });
                item.add(new ListView<String>("release_distributions", Arrays.asList(item.getModelObject().getDistributionArray())) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        ExternalLink link = new ExternalLink("release_distribution", item.getModelObject()){
                            @Override
                            public IModel<?> getBody() {
                                return Model.of(item.getModelObject());
                            }
                        };
                        link.add(AttributeModifier.append("target", "_blank"));
                        item.add(link);
                    }
                });
                item.add(new Label("release_createTime",new Date(item.getModel().getObject().getCreateTime()).toString()));
                if(releases.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }
            }
        });
    }
}


