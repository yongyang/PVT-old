package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.DataTable;
import com.googlecode.wicket.kendo.ui.datatable.column.CurrencyPropertyColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;

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
    }
}


