package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;

/**
 * Created by yyang on 4/22/15.
 */
@JsonAutoDetect
//@JsonIgnoreProperties({"jenkinsConfigFile"})
public class JenkinsJobConfig extends JobConfig {

    /**
     * the config.xml path
     */
    private String jenkinsConfigFile;

    JenkinsJobConfig() {

    }
}
