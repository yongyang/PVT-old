package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.File;

/**
 * Created by yyang on 4/22/15.
 */
@JsonAutoDetect
public class JenkinsJobConfig extends JobConfig {

    /**
     * the config.xml path
     */
    private File jenkinsConfigFile;


    JenkinsJobConfig() {
    }
}
