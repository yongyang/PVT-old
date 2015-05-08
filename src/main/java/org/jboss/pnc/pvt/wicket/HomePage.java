package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.link.Link;

/**
 * Created by yyang on 4/30/15.
 */
public class HomePage extends TemplatePage{

    public HomePage() {
        super();


        add(new Link<String>("link-release") {
            @Override
            public void onClick() {
                setResponsePage(ReleasePage.class);
            }
        });
    }
}
