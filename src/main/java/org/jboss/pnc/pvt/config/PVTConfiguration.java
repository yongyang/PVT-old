package org.jboss.pnc.pvt.config;

import org.jboss.pnc.pvt.config.JobConfig;
import org.jboss.pnc.pvt.config.ProductConfig;

import java.util.ArrayList;
import java.util.List;

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

    private List<ProductConfig> productConfigs = new ArrayList<>();
    private List<JobConfig> jobConfigs = new ArrayList<>();


    public PVTConfiguration() {
        init();
    }

    private void init() {
        loadProductConfigs();
        loadJobConfigs();
    }

    private void loadProductConfigs() {

    }

    private void loadJobConfigs() {

    }

    public ProductConfig newProductConfig() {
        return new ProductConfig();
    }

    public void addProductConfig(ProductConfig productConfig) {

    }

    public ProductConfig getProductConfig(String productName) {
        return null;
    }

    public void saveProductConfig(ProductConfig productName){

    }

    public void save() {

    }

}
