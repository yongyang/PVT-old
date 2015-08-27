package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.Product;

import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ProductsPage extends TemplatePage{

    public ProductsPage(PageParameters pp) {
        this(pp,"PVT products loaded.");
    }

    public ProductsPage(PageParameters pp, String info) {
    	super(pp, info);
        setActiveMenu(Menu.PRODUCTS);
/*
        add(new Link<String>("link-product") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(ReleasePage.class, pp);
            }
        });
*/

        add(new Link<String>("link-newproduct") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                setResponsePage(ProductNewPage.class, pp);
            }
        });


        List<Product> products = ((PVTApplication)Application.get()).getDAO().getPvtModel().getProducts();

        add(new Label("product_count", Model.of("" + products.size())));

        add(new ListView<Product>("product_rows", products) {
            @Override
            protected void populateItem(ListItem<Product> item) {
                item.add(new Link<String>("product_link") {
                    @Override
                    public void onClick() {
                    	PageParameters pp = new PageParameters();
                        pp.set("productId", item.getModel().getObject().getId());
                        setResponsePage(ProductEditPage.class,pp);
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "name");
                    }
                });
//                item.add(new Label("product_name", new PropertyModel(item.getModel(), "name")));
                item.add(new Label("product_maintainer", new PropertyModel(item.getModel(), "maintainer")));
                item.add(new Label("product_developer", new PropertyModel(item.getModel(), "developer")));
                item.add(new Label("product_qe", new PropertyModel(item.getModel(), "qe")));
                item.add(new Label("product_description", new PropertyModel(item.getModel(), "description")));
                if(products.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }
            }
        });

    }

}


