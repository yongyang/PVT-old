package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import java.util.Calendar;

/**
 * Created by yyang on 4/29/15.
 */
public abstract class TemplatePage extends WebPage {

    public TemplatePage() {
        super();

        final Label timeLabel = new Label("time", new Model<String>() {
            @Override
            public String getObject() {
                return Calendar.getInstance().getTime().toString();
            }
        });

        timeLabel.add(new AbstractAjaxTimerBehavior(Duration.seconds(1L)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                target.add(timeLabel);
            }
        });

        add(timeLabel);

        add(new Link<String>("link-releases") {
            @Override
            public void onClick() {
                setResponsePage(ReleasesPage.class);
            }
        });

        add(new Link<String>("link-products") {
            @Override
            public void onClick() {
                setResponsePage(ProductsPage.class);
            }
        });

        final WebMarkupContainer messagePanel = new WebMarkupContainer("messagePanel");
        messagePanel.add( new Link("closeButton") {
            @Override
            public void onClick() {
                messagePanel.setVisible(false);
            }
        });
        add(messagePanel);
    }
}
