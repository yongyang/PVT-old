package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by yyang on 4/30/15.
 */
public class HomePage extends TemplatePage{

    public HomePage() {
        super();


        add(new Link<String>("link-release") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(ReleasePage.class, pp);
            }
        });
    }
}
