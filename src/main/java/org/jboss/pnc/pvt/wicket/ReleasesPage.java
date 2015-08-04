package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.DataTable;
import com.googlecode.wicket.kendo.ui.datatable.column.CurrencyPropertyColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;

import java.io.Serializable;
import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ReleasesPage extends TemplatePage{

    public ReleasesPage() {
        this("PVT releases loaded.");
    }

    public ReleasesPage(String info) {
        super(info);
        setActiveMenu("releases");

/*
        add(new Link<String>("link-release") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1, "b");
                pp.add("c", "d");
                setResponsePage(ReleasePage.class, pp);
            }
        });
*/


        add(new Link<String>("link-newrelease") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1, "b");
                pp.add("c", "d");
                setResponsePage(NewReleasePage.class, pp);
            }
        });

        List<Release> releases = ((PVTApplication) Application.get()).getDAO().getPvtModel().getReleases();
        add(new Label("releases_count", Model.of("" + releases.size())));

        add(new ListView<Release>("release_rows", releases) {
            @Override
            protected void populateItem(ListItem<Release> item) {
                item.add(new Link<String>("product_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set(0, item.getModel().getObject().getName());
                        Session.get().setAttribute("product", item.getModel().getObject());
                        setResponsePage(ProductPage.class,pp);
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "productName");
                    }
                });

                item.add(new Link<String>("release_link") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.set(0, item.getModel().getObject().getName());
                        Session.get().setAttribute("product", item.getModel().getObject());
                        setResponsePage(ProductPage.class,pp);
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


