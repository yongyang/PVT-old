package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
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

    public static PVTApplication pvtApplication;

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

        pvtApplication = (PVTApplication)Application.get();

        // add your configuration here
    }

    public static PVTDataAccessObject getDAO() {
        return get().getMetaData(DAO_KEY);
    }

    public static PVTApplication get(){
        if(pvtApplication == null) {
            throw new RuntimeException("Call to early, PVT application not initialized yet!!!");
        }
        return pvtApplication;
    }

    public String getHttpURLBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        String host = System.getProperty("pvt.http.host", System.getProperty("jboss.bind.address", "localhost"));

        sb.append(host);
        String port = System.getProperty("pvt.http.port", "8080");
        if (!"80".equals(port)) {
            sb.append(":");
            sb.append(port);
        }
        return sb.toString();
    }

    public String getRESTURLBase() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHttpURLBase());
        sb.append(pvtApplication.getServletContext().getContextPath());
        return sb.toString();
    }
}