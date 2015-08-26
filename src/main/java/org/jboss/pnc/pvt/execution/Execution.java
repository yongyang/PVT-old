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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Log of the execution, maybe changed constantly before execution is finished.
     */
    private String log;

    /**
     * Status of the Execution. Default to Status.RUNNING.
     */
    private Status status = Status.RUNNING;

    private final List<CallBack> callBacks = new ArrayList<>();

    public Execution(final String name) {
        super();
        this.name = name;
    }

    public void addCallBack(CallBack callBack) {
        if (callBack != null) {
            callBacks.add(callBack);
        }
    }

    public void removeCallBack(CallBack callBack) {
        callBacks.remove(callBack);
    }

    public void cleanCallBacks() {
        callBacks.clear();
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        if (log != null && !log.equals(this.log)) {
            this.log = log;
            for (CallBack callBack: callBacks) {
                callBack.onLogChanged(this);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        if (status != null && ! status.equals(this.status)) {
            this.status = status;
            for (CallBack callBack: callBacks) {
                callBack.onStatus(this);
            }
        }
    }

    public static interface CallBack extends Serializable {

        void onStatus(Execution execution);

        void onLogChanged(Execution execution);
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
        public String getJobContent() {
            return jobContent;
        }

        /**
         * @return the jobParams
         */
        public Map<String, String> getJobParams() {
            return jobParams;
        }

    }

    static class JVMExecution extends Execution {

        private static final long serialVersionUID = 1L;

        private final Runnable runnable;

        JVMExecution(final String name, final Runnable runnable) {
            super(name);
            this.runnable = runnable;
        }

        /**
         * @return the runnable
         */
        public Runnable getRunnable() {
            return runnable;
        }

    }

    public static Execution createJenkinsExecution(final String jobId) {
        return new JenkinsExecution(jobId, null, null);
    }

    public static Execution createJenkinsExecution(final String jobId, final Map<String, String> jobParams) {
        return new JenkinsExecution(jobId, null, jobParams);
    }

    public static Execution createJenkinsExecution(final String jobId, final String jobContent, final Map<String, String> jobParams) {
        return new JenkinsExecution(jobId, jobContent, jobParams);
    }

    public static Execution createJVMExecution(final String name, final Runnable runnable) {
        return new JVMExecution(name, runnable);
    }
}
