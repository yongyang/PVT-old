package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping product configuration in gitdb
 *
 * Created by yyang on 4/16/15.
 */
@JsonAutoDetect
public class ProductConfig {

    private String name;
    private String version;
    private String release;

    private List<JobConfig> jobConfigs = new ArrayList<>();


    ProductConfig() {
    }
}
