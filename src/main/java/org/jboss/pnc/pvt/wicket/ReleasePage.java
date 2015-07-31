package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by yyang on 5/5/15.
 */
public class ReleasePage extends TemplatePage {

    public ReleasePage() {
        this("PVT release loaded!");
    }

    public ReleasePage(String info) {
        super(info);

        add(new Link<String>("link-newrelease") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set(0, "a");
                pp.set(1,"b");
                pp.add("c","d");
                setResponsePage(NewReleasePage.class, pp);
            }
        });
    }
}
