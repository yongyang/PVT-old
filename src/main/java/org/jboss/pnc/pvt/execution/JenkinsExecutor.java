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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution.JenkinsExecution;
import org.jboss.pnc.pvt.report.Report;
import org.jboss.pnc.pvt.report.Report.ReportLog;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Artifact;
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
    
    private final JenkinsConfiguration jenkinsConfig;

    JenkinsExecutor(JenkinsConfiguration jenkinsConfig) {
        super();
        try {
            if (jenkinsConfig == null) {
                this.jenkinsConfig = getDefaultJenkinsProps();
            } else {
                this.jenkinsConfig = jenkinsConfig;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't initialize Jenkins Executor.", e);
        }

    }

    private JenkinsServer getJenkinsServer() throws IOException {
        String jenkinsUrl = this.jenkinsConfig.getUrl();
        String username = this.jenkinsConfig.getUsername();
        String password = this.jenkinsConfig.getPassword();
        if (jenkinsUrl == null || jenkinsUrl.trim().length() == 0) {
            throw new IllegalStateException("Jenkins URL must be specified.");
        }
        final JenkinsHttpClient jenkinsHttpClient;
        try {
            if (username != null && username.trim().length() > 0
                    && password != null && password.trim().length() > 0) {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl), username, password);
            } else {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl));
            }
        } catch (URISyntaxException e) {
            throw new IOException("Wrong JenkinsURL: " + jenkinsUrl, e);
        }
        return new JenkinsServer(jenkinsHttpClient);
    }

    @Override
    public void execute(final Execution execution, final CallBack callBack) throws ExecutionException {
        JenkinsExecution jenkinsExe = (JenkinsExecution)execution;
        jenkinsExe.setReport(new Report());  // report will be initialized when it is started.
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
            startMonitor(jobId, buildNumber, execution, callBack);
        } catch (IOException e) {
            execution.setException(e);
            if (callBack != null) {
                callBack.onException(execution);
            }
            throw new ExecutionException(String.format("Failed to execute Jenkins job: %s", jobId), e);
        }
    }

    private void startMonitor(String jobName, int buildNumber, final Execution execution, final CallBack callBack) {
        AtomicInteger statusRetrieveFailed = new AtomicInteger(0);
        final Runnable checking = new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();

                try {
                    Build jenkinsBuild = getBuild(jobName, buildNumber);
                    if (jenkinsBuild == null) {
                        getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                        return;
                    }

                    BuildWithDetails jenkinsBuildDetails = jenkinsBuild.details();
                    String log = jenkinsBuildDetails.getConsoleOutputText();
                    if (log != null) {
                        execution.getReport().setMainLog(log);
                        if (callBack != null) {
                            callBack.onLogChanged(execution);
                        }
                    }
                    BuildResult result = jenkinsBuildDetails.getResult();
                    long timeout = jenkinsConfig.getJobTimeOut();
                    if (timeout > 0) {
                        long end = System.currentTimeMillis();
                        if (end - start >= timeout) {
                            throw new RuntimeException("Timeout to check build status of Jenkins job: " + jobName);
                        }
                    }
                    if (result == null) {
                        getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                        return;
                    }
                    switch (result) {
                        case BUILDING:
                        case REBUILDING:
                        {
                            execution.setStatus(Execution.Status.RUNNING);
                            if (callBack != null) {
                                callBack.onStatus(execution);
                            }
                            getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                            break;
                        }
                        case FAILURE:
                        case UNSTABLE:
                        case ABORTED:
                        {
                            execution.setStatus(Execution.Status.FAILED);
                            List<Artifact> artiFacts = jenkinsBuildDetails.getArtifacts();
                            if (artiFacts != null && artiFacts.size() > 0) {
                                for (Artifact arti: artiFacts) {
                                    try(InputStream input = jenkinsBuildDetails.downloadArtifact(arti)) {
                                        String artiLog = IOUtils.toString(input);
                                        ReportLog reportLog = new ReportLog(arti.getFileName(), artiLog);
                                        execution.getReport().addReportLog(reportLog);
                                    }
                                }
                                if (callBack != null) {
                                    callBack.onLogChanged(execution);
                                }
                            }
                            if (callBack != null) {
                                callBack.onStatus(execution);
                                callBack.onTerminated(execution);
                            }
                            break;
                        }
                        case SUCCESS:
                        {
                            execution.setStatus(Execution.Status.SUCCEEDED);
                            List<Artifact> artiFacts = jenkinsBuildDetails.getArtifacts();
                            if (artiFacts != null && artiFacts.size() > 0) {
                                for (Artifact arti: artiFacts) {
                                    try(InputStream input = jenkinsBuildDetails.downloadArtifact(arti)) {
                                        String artiLog = IOUtils.toString(input);
                                        ReportLog reportLog = new ReportLog(arti.getFileName(), artiLog);
                                        execution.getReport().addReportLog(reportLog);
                                    }
                                }
                                if (callBack != null) {
                                    callBack.onLogChanged(execution);
                                }
                            }
                            if (callBack != null) {
                                callBack.onStatus(execution);
                                callBack.onTerminated(execution);
                            }
                            break;
                        }
                        default:
                        {
                            execution.setStatus(Execution.Status.UNKNOWN);
                            if (callBack != null) {
                                callBack.onStatus(execution);
                            }
                            getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                            break;
                        }
                    }
                } catch (IOException e) {
                    execution.setException(e);
                    if (callBack != null) {
                        callBack.onException(execution);
                    }
                    int failed = statusRetrieveFailed.getAndIncrement();
                    if (failed >= getMaxRetryTime()) {
                        logger.error("Failed to check Build Detail. Give up!", e);
                        if (callBack != null) {
                            callBack.onTerminated(execution);
                        }
                    } else {
                        logger.warn("Failed to check Build Detail. Continue.", e);
                        getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                    }
                } catch (Throwable t) {
                    logger.error("Failed to Monitor the execution.", t);
                    if (callBack != null) {
                        callBack.onTerminated(execution);
                    }
                }
            }

        };
        getMonitorExecutorService().schedule(checking, getMonitorInterval(), TimeUnit.SECONDS);
    }

    private Build getBuild(String jobName, int buildNumber) throws IOException {
        JenkinsServer jenkinsServer = getJenkinsServer();
        JobWithDetails jenkinsJob = jenkinsServer.getJob(jobName);
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
        JenkinsServer jenkinsServer = getJenkinsServer();
        JobWithDetails jenkinsJob = jenkinsServer.getJob(jobName);
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
        JenkinsServer jenkinsServer = getJenkinsServer();
        if (jobContent == null) {
            throw new ExecutionException("No job content found for: " + jobName);
        }
        jenkinsServer.updateJob(jobName, jobContent, this.jenkinsConfig.isCrumbFlag());
        return jenkinsServer.getJob(jobName);
    }

    /**
     * Creates jenkins job, check crumbFlag
     * 
     */
    private JobWithDetails createJenkinsJob(String jobName, String jobContent) throws IOException, ExecutionException {
        JenkinsServer jenkinsServer = getJenkinsServer();
        if (jobContent == null) {
            throw new ExecutionException("No job content found for: " + jobName);
        }
        jenkinsServer.createJob(jobName, jobContent, this.jenkinsConfig.isCrumbFlag());
        return jenkinsServer.getJob(jobName);
    }

}
