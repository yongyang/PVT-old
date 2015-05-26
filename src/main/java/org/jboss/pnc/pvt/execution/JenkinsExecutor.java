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
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
class JenkinsExecutor implements Executor {
    
    static JenkinsExecutor INSTANCE = new JenkinsExecutor(null);
    
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    
    private static final Logger logger = Logger.getLogger(JenkinsExecutor.class);
    
    private final JenkinsHttpClient jenkinsHttpClient;
    private final JenkinsServer jenkinsServer;
    
    private Boolean crumbFlag = null;
    
    private final Properties props;
    
    JenkinsExecutor(Properties props) {
        super();
        try {
            if (props == null) {
                this.props = getDefaultJenkinsProps();
            } else {
                this.props = props;
            }
            String jenkinsUrl = getJenkinsURL();
            String username = getJenkinsUsername();
            String password = getJenkinsPassword();
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

    private String getJenkinsPassword() {
        return props.getProperty("jenkins.password");
    }

    private String getJenkinsUsername() {
        return props.getProperty("jenkins.username");
    }

    private String getJenkinsURL() {
        return props.getProperty("jenkins.server.url");
    }
    
    /**
     * Jenkins Job submitted timeout, default to 30 seconds
     */
    private long getJenkinsJobSubmittedTimeout() {
        return Long.valueOf(props.getProperty("jenkins.job.submit.timeout", "30000"));
    }

    /**
     * Jenkins Job submitted timeout, default to -1, means forever
     */
    private long getJenkinsJobCompleteTimeout() {
        return Long.valueOf(props.getProperty("jenkins.job.complete.timeout", "-1"));
    }

    private boolean createJenkinsJobIfMissing() {
        return Boolean.valueOf(props.getProperty("jenkins.job.create.missing", "False"));
    }

    private boolean overrideJenkinsJob() {
        return Boolean.valueOf(props.getProperty("jenkins.job.override", "False"));
    }

    private Properties getDefaultJenkinsProps() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/jenkins.properties")) {
            props.load(input);
        }
        return props;
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

    JobMapper getJobMapper() {
        return JobMapper.DEFAULT;
    }

    @Override
    public Execution execute(String productName, String version, String toolName) throws ExecutionException {
        Future<Execution> future = execute(productName, version, toolName, Stage.Initial);
        try {
            return future.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Failed to execute toolName: " + toolName, e);
        }
    }

    @Override
    public Future<Execution> execute(String productName, String version, String toolName, Stage stage)
            throws ExecutionException {
        String jobName = getJobMapper().getJobName(productName, version, toolName);
        try {
            checkJenkinsCrumbFlag();
            JobWithDetails jenkinsJob = getOrCreateJenkinsJob(jobName, toolName);
            if (jenkinsJob == null) {
                throw new ExecutionException("No Jenkins job: " + jobName + " found.");
            }
        } catch (IOException e) {
            throw new ExecutionException("Can't create or get job name: " + jobName, e);
        }
        
        return execute(jobName, stage);
    }

    @Override
    public Execution execute(String productName, String version, String toolName, Map<String, String> params)
            throws ExecutionException {
        Future<Execution> future = execute(productName, version, toolName, null, Stage.Initial);
        try {
            return future.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Failed to execute toolName: " + toolName, e);
        }
    }

    @Override
    public Future<Execution> execute(String productName, String version, String toolName, Map<String, String> params,
            Stage stage) throws ExecutionException {
        String jobName = getJobMapper().getJobName(productName, version, toolName);
        try {
            checkJenkinsCrumbFlag();
            JobWithDetails jenkinsJob = getOrCreateJenkinsJob(jobName, toolName);
            if (jenkinsJob == null) {
                throw new ExecutionException("No Jenkins job: " + jobName + " found.");
            }
        } catch (IOException e) {
            throw new ExecutionException("Can't create or get job name: " + jobName, e);
        }
        return execute(jobName, params, stage);
    }

    @Override
    public Execution execute(String jobName) throws ExecutionException {
        Future<Execution> future = execute(jobName, Stage.Initial);
        try {
            return future.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Failed to execute jobName: " + jobName, e);
        }
    }

    @Override
    public Execution execute(String jobName, Map<String, String> params) throws ExecutionException {
        Future<Execution> future = execute(jobName, params, Stage.Initial);
        try {
            return future.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Failed to execute jobName: " + jobName, e);
        }
    }

    @Override
    public Future<Execution> execute(final String jobName, final Map<String, String> params, final Stage stage) throws ExecutionException {
        Objects.requireNonNull(jobName, "Job Name can not be null");
        Objects.requireNonNull(stage, "stage can not be null");
        try {
            checkJenkinsCrumbFlag();
            JobWithDetails jenkinsJob = this.jenkinsServer.getJob(jobName);
            if (jenkinsJob == null) {
                throw new ExecutionException("No Jenkins job: " + jobName + " found.");
            }
            logger.info("Executing Jenkins Job: " + jobName);
            final int buildNumber = buildJenkinsJob(jenkinsJob, params);

            final Execution execution = new Execution();
            execution.setJobName(jobName);
            execution.setNumber(buildNumber);

            StringBuilder url = new StringBuilder(getJenkinsURL());
            url.append("/job/");
            url.append(jobName);
            url.append("/");
            url.append(buildNumber);
            url.append("/");
            execution.setLink(url.toString());
            return executor.submit(new Callable<Execution>() {
                @Override
                public Execution call() throws Exception {
                    switch (stage) {
                        case Initial:
                            return execution;
                        case Submit:
                        case Prepare:
                        case Running:
                            waitRunning(jobName, buildNumber);
                            return execution;
                        case Complet:
                            waitComplete(execution, jobName, buildNumber);
                            return execution;
                        default:
                            break;
                    }
                    return execution;
                }

            });
        } catch (IOException e) {
            throw new ExecutionException("Can't create/update Jenkins job: " + jobName, e);
        }
    }

    private int buildJenkinsJob(JobWithDetails jenkinsJob, Map<String, String> params) throws IOException {
        int buildNumber = jenkinsJob.getNextBuildNumber();
        if (params != null && !params.isEmpty()) {
            jenkinsJob.build(params);
        } else {
            jenkinsJob.build();
        }
        return buildNumber;
    }

    @Override
    public Future<Execution> execute(final String jobName, final Stage stage) throws ExecutionException {
        return execute(jobName, null, stage);
    }

    private void waitRunning(String jobName, int buildNumber) throws TimeoutException {
        long timeout = getJenkinsJobSubmittedTimeout();
        long start = System.currentTimeMillis();
        while(true) {
            try {
                JobWithDetails job = jenkinsServer.getJob(jobName);
                Build build = getBuild(job, buildNumber);
                if (build != null) {
                    return;
                }
                long end = System.currentTimeMillis();
                if (timeout > 0 && end - start >= timeout) {
                    // timeout
                    throw new TimeoutException(jobName + " is not started after: " + timeout / 1000 + " seconds");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    return;
                }
            } catch (IOException e) {
                continue;
            }
        }
    }
    
    private void waitComplete(Execution execution, String jobName, int buildNumber) throws TimeoutException {
        long timeout = getJenkinsJobCompleteTimeout();
        long start = System.currentTimeMillis();
        while(true) {
            try {
                JobWithDetails job = jenkinsServer.getJob(jobName);
                Build build = getBuild(job, buildNumber);
                if (build != null) {
                    BuildWithDetails detail = build.details();
                    if (!detail.isBuilding() && detail.getConsoleOutputText() != null) {
                        execution.setLog(detail.getConsoleOutputHtml());
                        return;
                    }
                }
                long end = System.currentTimeMillis();
                if (timeout > 0 && end - start >= timeout) {
                    // timeout
                    throw new TimeoutException(jobName + " is not completed after: " + timeout / 1000 + " seconds");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    return;
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * Gets or create a job by its name
     */
    private JobWithDetails getOrCreateJenkinsJob(String jobName, String toolName) throws IOException, ExecutionException {
        JobWithDetails jenkinsJob = this.jenkinsServer.getJob(jobName);
        String jobContent = getJobMapper().getJobXMLContent(toolName);
        if (jenkinsJob == null && createJenkinsJobIfMissing()) {
            jenkinsJob = createJenkinsJob(jobName, jobContent);
        } else {
            if (overrideJenkinsJob()) {
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

    private Build getBuild(JobWithDetails job, int buildNumber) {
        for (Build build: job.getBuilds()) {
            if (buildNumber == build.getNumber()) {
                return build;
            }
        }
        return null;
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }


}
