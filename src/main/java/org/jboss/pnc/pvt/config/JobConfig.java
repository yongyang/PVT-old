package org.jboss.pnc.pvt.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * POJO class to map job metadata stored in gitdb
 *
 * Created by yyang on 4/16/15.
 */
@JsonAutoDetect
public class JobConfig {

    private String name;

    JobConfig() {

    }
}
