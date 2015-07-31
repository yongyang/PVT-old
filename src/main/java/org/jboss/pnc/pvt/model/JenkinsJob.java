package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
public class JenkinsJob {
    /*
    * the config.xml path
    */
    private String configFile;

    public JenkinsJob() {

    }
}
