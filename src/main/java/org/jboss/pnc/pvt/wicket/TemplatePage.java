package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

/**
 * Created by yyang on 4/29/15.
 */
public abstract class TemplatePage extends WebPage {

    public TemplatePage() {
        super();

        add(new Link<String>("link-releases") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });
    }
}
