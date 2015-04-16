package org.jboss.pnc.pvt.git;

/**
 * PVTConfiguration loads all needed configurations, runtime datas from git db
 *
 * and provide api to modify
 *
 *
 *
 * Created by yyang on 4/16/15.
 */
public class PVTConfiguration {

    private PVTConfiguration() {
        init();
    }

    private void init() {
        //TODO: load configurations from gitdb
    }

    public static PVTConfiguration load() {
        return new PVTConfiguration();
    }


}
