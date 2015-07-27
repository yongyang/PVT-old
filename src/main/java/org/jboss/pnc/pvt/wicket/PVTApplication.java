package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.pnc.pvt.dao.JSONDataAccessObject;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;

/**
 * Created by yyang on 4/30/15.
 */
public class PVTApplication extends WebApplication
{

    public static final MetaDataKey<? extends PVTDataAccessObject> DAO_KEY = new MetaDataKey<JSONDataAccessObject> (){};

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {

        return ReleasesPage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init()
    {
        super.init();
        PVTDataAccessObject dao = new JSONDataAccessObject();
        dao.load();
        setMetaData(DAO_KEY, dao);

        // add your configuration here
    }

    public PVTDataAccessObject getDAO() {
        return getMetaData(DAO_KEY);
    }
}

