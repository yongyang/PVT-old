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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@XmlRootElement(name = "execution")
@JsonAutoDetect
public abstract class Execution implements Serializable {

    private static final long serialVersionUID = 3940491733119462766L;

    /** Name of the execution **/
    @XmlAttribute
    private final String name;

    /** Each execution should have an external link. **/
    private String link;

    /** Exception when there is an Exception occured. **/
    private Exception exception;

    /**
     * Log of the execution, maybe changed constantly before execution is finished.
     */
    private String log;

    /**
     * Status of the Execution. Default to Status.RUNNING.
     */
    private Status status = Status.RUNNING;

    public Execution(String name) {
        super();
        this.name = name;
        Objects.requireNonNull(name, "Each Execution must have a name.");
    }

    /**
     * @return the exception
     */
    public synchronized Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public synchronized Execution setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public synchronized String getLog() {
        return log;
    }

    public synchronized Execution setLog(String log) {
        this.log = log;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public Execution setLink(String link) {
        this.link = link;
        return this;
    }

    /**
     * @return the status
     */
    public synchronized Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public synchronized Execution setStatus(Status status) {
        this.status = status;
        return this;
    }

    public static enum Status {
        RUNNING,
        FAILED,
        SUCCEEDED,
        UNKNOWN
    }

    static class JenkinsExecution extends Execution {

        private static final long serialVersionUID = 1L;

        private final String jobContent;
        private final Map<String, String> jobParams;

        JenkinsExecution(String jobId, String jobContent, Map<String, String> jobParams) {
            super(jobId);
            this.jobContent = jobContent;
            this.jobParams = jobParams;
        }

        /**
         * @return the jobContent
         */
        String getJobContent() {
            return jobContent;
        }

        /**
         * @return the jobParams
         */
        Map<String, String> getJobParams() {
            return jobParams;
        }

    }

    static class JVMExecution extends Execution {

        private static final long serialVersionUID = 1L;

        private final transient ExecutionRunnable runnable;

        JVMExecution(final String name, final ExecutionRunnable runnable) {
            super(name);
            this.runnable = runnable;
            this.runnable.setExecution(this);
        }

        /**
         * @return the runnable
         */
        @JsonIgnore
        ExecutionRunnable getRunnable() {
            return runnable;
        }

    }

    /**
     * Creates a Jenkins Execution with a jobId.
     * 
     * @param jobId the Jenkins job name. Not null
     * @return a Jenkins Execution which handles the status changes.
     */
    public static Execution createJenkinsExecution(final String jobId) {
        return new JenkinsExecution(jobId, null, null);
    }

    public static Execution createJenkinsExecution(final String jobId, final Map<String, String> jobParams) {
        return new JenkinsExecution(jobId, null, jobParams);
    }

    public static Execution createJenkinsExecution(final String jobId, final String jobContent, final Map<String, String> jobParams) {
        return new JenkinsExecution(jobId, jobContent, jobParams);
    }

    public static Execution createJVMExecution(final String name, final ExecutionRunnable runnable) {
        return new JVMExecution(name, runnable);
    }
}
