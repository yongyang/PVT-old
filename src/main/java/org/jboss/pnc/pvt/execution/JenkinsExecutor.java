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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution.JenkinsExecution;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
class JenkinsExecutor extends Executor {

    
    static JenkinsExecutor INSTANCE = new JenkinsExecutor(null);
    
    private static final Logger logger = Logger.getLogger(JenkinsExecutor.class);
    
    private final JenkinsHttpClient jenkinsHttpClient;
    private final JenkinsServer jenkinsServer;

    private Boolean crumbFlag = null;

    private final JenkinsConfiguration jenkinsConfig;

    JenkinsExecutor(JenkinsConfiguration jenkinsConfig) {
        super();
        try {
            if (jenkinsConfig == null) {
                this.jenkinsConfig = getDefaultJenkinsProps();
            } else {
                this.jenkinsConfig = jenkinsConfig;
            }
            String jenkinsUrl = this.jenkinsConfig.getUrl();
            String username = this.jenkinsConfig.getUsername();
            String password = this.jenkinsConfig.getPassword();
            if (jenkinsUrl == null || jenkinsUrl.trim().length() == 0) {
                throw new IllegalStateException("Jenkins URL must be specified.");
            }
            if (username != null && username.trim().length() > 0
                    && password != null && password.trim().length() > 0) {
                this.jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl), username, password);
            } else {
                this.jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl));
            }
            this.jenkinsServer = new JenkinsServer(jenkinsHttpClient);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Can't initialize Jenkins Executor.", e);
        }

    }

    private JenkinsConfiguration getDefaultJenkinsProps() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/jenkins.properties")) {
            props.load(input);
        }
        return JenkinsConfiguration.fromProperty(props);
    }

    private void checkJenkinsCrumbFlag() {
        if (this.crumbFlag != null) {
            return;
        }
        try {
            jenkinsHttpClient.get("/crumbIssuer/api/xml");
            this.crumbFlag = Boolean.TRUE;
        } catch (IOException e) {
            logger.debug("Jenkins is not protected with CSRF", e);
            this.crumbFlag = Boolean.FALSE;
        }
    }

    @Override
    public void execute(final Execution execution) throws ExecutionException {
        JenkinsExecution jenkinsExe = (JenkinsExecution)execution;
        String jobId = jenkinsExe.getName();
        String jobContent = jenkinsExe.getJobContent();
        try {
            JobWithDetails jobDetail = getOrCreateJenkinsJob(jobId, jobContent);
            if (jobDetail == null) {
                throw new ExecutionException("Jenkins Job: " + jobId + " does not exist.");
            }
            final int buildNumber = buildJenkinsJob(jobDetail, jenkinsExe.getJobParams());

            StringBuilder url = new StringBuilder(this.jenkinsConfig.getUrl());
            url.append("/job/");
            url.append(jobId);
            url.append("/");
            url.append(buildNumber);
            url.append("/");
            execution.setLink(url.toString());
            startMonitor(jobId, buildNumber, execution);
        } catch (IOException e) {
            throw new ExecutionException(String.format("Failed to execute Jenkins job: %s", jobId), e);
        }
    }

    private void startMonitor(String jobName, int buildNumber, final Execution execution) {
        final ObjectWrapper<ScheduledFuture<?>> objectHolder = new ObjectWrapper<>();
        Runnable checking = () -> {
            long start = System.currentTimeMillis();

            try {
                Build jenkinsBuild = getBuild(jobName, buildNumber);
                if (jenkinsBuild == null) {
                    return;
                }

                BuildWithDetails jenkinsBuildDetails = jenkinsBuild.details();
                String log = jenkinsBuildDetails.getConsoleOutputText();
                if (log != null) {
                    execution.setLog(log);
                }
                BuildResult result = jenkinsBuildDetails.getResult();
                long timeout = this.jenkinsConfig.getJobTimeOut();
                if (timeout > 0) {
                    long end = System.currentTimeMillis();
                    if (end - start >= timeout) {
                        throw new RuntimeException("Timeout to check build status of Jenkins job: " + jobName);
                    }
                }
                if (result == null) {
                    return;
                }
                switch (result) {
                    case BUILDING:
                    case REBUILDING:
                    {
                        execution.setStatus(Execution.Status.RUNNING);
                        break;
                    }
                    case FAILURE:
                    case UNSTABLE:
                    case ABORTED:
                    {
                        execution.setStatus(Execution.Status.FAILED);
                        objectHolder.get().cancel(false);
                        break;
                    }
                    case SUCCESS:
                    {
                        execution.setStatus(Execution.Status.SUCCEEDED);
                        objectHolder.get().cancel(false);
                        break;
                    }
                    default:
                    {
                        execution.setStatus(Execution.Status.UNKNOWN);
                        objectHolder.get().cancel(false);
                        break;
                    }
                }
            } catch (IOException e) {
                logger.info("Failed to check Build Detail, continue...", e);
            }
            
        };
        ScheduledFuture<?> future = getCheckingExecutorService().scheduleAtFixedRate(checking, 0, 10L, TimeUnit.SECONDS);
        objectHolder.set(future);
    }

    private Build getBuild(String jobName, int buildNumber) throws IOException {
        JobWithDetails jenkinsJob = this.jenkinsServer.getJob(jobName);
        if (jenkinsJob != null) {
            for (Build build: jenkinsJob.getBuilds()) {
                if (buildNumber == build.getNumber()) {
                    return build;
                }
            }
        }
        return null;
    }

    private int buildJenkinsJob(JobWithDetails jobDetail, Map<String, String> params) throws IOException {
        int buildNumber = jobDetail.getNextBuildNumber();
        if (params != null && !params.isEmpty()) {
            jobDetail.build(params);
        } else {
            jobDetail.build();
        }
        return buildNumber;
    }

    /**
     * Gets or create a job by its name
     */
    private JobWithDetails getOrCreateJenkinsJob(String jobName, String jobContent) throws IOException, ExecutionException {
        JobWithDetails jenkinsJob = this.jenkinsServer.getJob(jobName);
        if (jenkinsJob == null && this.jenkinsConfig.isCreateIfJobMissing()) {
            logger.info("Start to create the Jenkins job");
            jenkinsJob = createJenkinsJob(jobName, jobContent);
        } else {
            if (this.jenkinsConfig.isOverrideJob()) {
                jenkinsJob = updateJenkinsJob(jobName, jobContent);
            }
        }
        return jenkinsJob;
    }

    private JobWithDetails updateJenkinsJob(String jobName, String jobContent) throws IOException, ExecutionException {
        checkJenkinsCrumbFlag();
        if (jobContent == null) {
            throw new ExecutionException("No job content found for: " + jobName);
        }
        this.jenkinsServer.updateJob(jobName, jobContent, crumbFlag);
        return jenkinsServer.getJob(jobName);
    }

    /**
     * Creates jenkins job, check crumbFlag
     * 
     */
    private JobWithDetails createJenkinsJob(String jobName, String jobContent) throws IOException, ExecutionException {
        checkJenkinsCrumbFlag();
        if (jobContent == null) {
            throw new ExecutionException("No job content found for: " + jobName);
        }
        this.jenkinsHttpClient.post_xml("/createItem?name=" + encode(jobName), jobContent, crumbFlag);
        return jenkinsServer.getJob(jobName);
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
}
