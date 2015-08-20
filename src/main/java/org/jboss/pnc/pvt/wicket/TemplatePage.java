package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import java.util.Calendar;

/**
 * Created by yyang on 4/29/15.
 */
public abstract class TemplatePage extends WebPage {

    private WebMarkupContainer releasesLi;
    private WebMarkupContainer productsLi;
    private WebMarkupContainer toolsLi;

    private final WebMarkupContainer messagePanel = new WebMarkupContainer("messagePanel");
    private final Model<String> infoModel = Model.of();

    public TemplatePage() {
        this("");
    }

    public TemplatePage(PageParameters pp) {
        this(pp, null);
    }

    public TemplatePage(String info) {
        this(null, info);
    }

    public TemplatePage(PageParameters pp, String info) {
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

        add(releasesLi = new WebMarkupContainer("li-releases"));
        releasesLi.add(new Link<String>("link-releases") {
            @Override
            public void onClick() {
                setResponsePage(ReleasesPage.class);
            }
        });


        add(productsLi = new WebMarkupContainer("li-products"));
        productsLi.add(new Link<String>("link-products") {
            @Override
            public void onClick() {
                setResponsePage(ProductsPage.class);
            }
        });


        add(toolsLi = new WebMarkupContainer("li-tools"));
        toolsLi.add(new Link<String>("link-tools") {
            @Override
            public void onClick() {
                setResponsePage(ToolsPage.class);
            }
        });

        messagePanel.add( new Link("closeButton") {
            @Override
            public void onClick() {
                messagePanel.setVisible(false);
            }
        });

        final Label infoLabel = new Label("message",infoModel);
        messagePanel.add(infoLabel);

        add(messagePanel);
        setInfo(info);

        setActiveMenu("releases");
    }

    public void setActiveMenu(String menu) {
        releasesLi.add(AttributeModifier.remove("class"));
        switch (menu) {
            case "releases":
                releasesLi.add(AttributeModifier.replace("class", "active"));
                break;
            case "products":
                productsLi.add(AttributeModifier.replace("class", "active"));
                break;
            case "tools":
                toolsLi.add(AttributeModifier.replace("class", "active"));
                break;
            default:
                releasesLi.add(AttributeModifier.replace("class", "active"));

        }
    }

    protected void setInfo(String info) {
        if (info != null && !info.isEmpty()) {
            infoModel.setObject(info);
        }
        messagePanel.setVisible(info != null && info.length() > 0);
    }

}
