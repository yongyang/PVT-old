/*
 * JBoss, Home of Professional Open Source
 * Copyright @year, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.pvt.execution;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Jenkins Configuration for a specified Jenkins server.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class JenkinsConfiguration {

    public static final String KEY_JENKINS_URL = "jenkins.server.url";
    public static final String KEY_JENKINS_USERNAME = "jenkins.server.username";
    public static final String KEY_JENKINS_PASSWORD = "jenkins.server.password";
    public static final String KEY_JENKINS_TIMEOUT = "jenkins.server.timeout";
    public static final String KEY_JENKINS_CREATE_JOB_IF_MISSING = "jenkins.job.create.missing";
    public static final String KEY_JENKINS_OVERRIDE_JOB = "jenkins.job.override";
    public static final String KEY_JENKINS_CRUMB_FlAG = "jenkins.crumb.flag";

    private String url;
    private String username;
    private String password;
    private long jobTimeOut;
    private boolean createIfJobMissing;
    private boolean overrideJob;
    private boolean crumbFlag;

    public static JenkinsConfiguration defaultJenkinsProps() throws IOException {
        Properties props = new Properties();
        try (InputStream input = Executor.class.getResourceAsStream("/jenkins.properties")) {
            props.load(input);
        }
        return JenkinsConfiguration.fromProperty(props);
    }

    public static JenkinsConfiguration fromProperty(Properties props) {
        JenkinsConfiguration config = new JenkinsConfiguration();
        config.setCreateIfJobMissing(Boolean.valueOf(props.getOrDefault(KEY_JENKINS_OVERRIDE_JOB, "False").toString()));
        config.setJobTimeOut(Long.valueOf(props.getOrDefault(KEY_JENKINS_TIMEOUT, "-1").toString())); // default to waiting forever
        config.setOverrideJob(Boolean.valueOf(props.getOrDefault(KEY_JENKINS_OVERRIDE_JOB, "False").toString()));
        config.setPassword(props.getOrDefault(KEY_JENKINS_PASSWORD, "").toString());
        config.setUrl(props.getOrDefault(KEY_JENKINS_URL, "").toString());
        config.setUsername(props.getOrDefault(KEY_JENKINS_USERNAME, "").toString());
        config.setCrumbFlag(Boolean.valueOf(props.getOrDefault(KEY_JENKINS_CRUMB_FlAG, "True").toString()));
        return config;
    }

    /**
     * @return the crumbFlag
     */
    public boolean isCrumbFlag() {
        return crumbFlag;
    }

    /**
     * @param crumbFlag the crumbFlag to set
     */
    public void setCrumbFlag(boolean crumbFlag) {
        this.crumbFlag = crumbFlag;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the jobTimeOut
     */
    public long getJobTimeOut() {
        return jobTimeOut;
    }

    /**
     * @param jobTimeOut the jobTimeOut to set
     */
    public void setJobTimeOut(long jobTimeOut) {
        this.jobTimeOut = jobTimeOut;
    }

    /**
     * @return the createIfJobMissing
     */
    public boolean isCreateIfJobMissing() {
        return createIfJobMissing;
    }

    /**
     * @param createIfJobMissing the createIfJobMissing to set
     */
    public void setCreateIfJobMissing(boolean createIfJobMissing) {
        this.createIfJobMissing = createIfJobMissing;
    }

    /**
     * @return the overrideJob
     */
    public boolean isOverrideJob() {
        return overrideJob;
    }

    /**
     * @param overrideJob the overrideJob to set
     */
    public void setOverrideJob(boolean overrideJob) {
        this.overrideJob = overrideJob;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JenkinsConfiguration [url=" + url + ", username=" + username + ", password=******, jobTimeOut="
                + jobTimeOut + ", createIfJobMissing=" + createIfJobMissing + ", overrideJob=" + overrideJob + ", crumbFlag="
                + crumbFlag + "]";
    }

}
