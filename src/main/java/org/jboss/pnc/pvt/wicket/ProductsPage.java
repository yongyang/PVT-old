package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.DataTable;
import com.googlecode.wicket.kendo.ui.datatable.column.CurrencyPropertyColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.Product;

import java.io.Serializable;
import java.util.*;

/**
 * Created by yyang on 4/30/15.
 */
public class ProductsPage extends TemplatePage{

    public ProductsPage() {
        this("PVT products loaded.");
    }

    public ProductsPage(String info) {
        super(info);
        setActiveMenu("products");
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
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(NewProductPage.class, pp);
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
                        pp.set(0, item.getModel().getObject().getName());
                        setResponsePage(ProductPage.class, pp);
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


