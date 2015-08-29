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

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public abstract class ExecutionRunnable implements Runnable {

    private static final Logger logger = Logger.getLogger(ExecutionRunnable.class);

    private Execution execution;

    private CallBack callBack;

    public Execution getExecution() {
        return this.execution;
    }

    void setExecution(Execution execution) {
        this.execution = execution;
    }

    void setCallback(CallBack callBack) {
        this.callBack = callBack;
    }

    public final void run() {
        if (this.execution == null) {
            throw new IllegalStateException("Wrong state. Execution is not set!!");
        }
        logger.debug("Running Execution: " + execution.getName());
        try {
            // before actual starting, set the status to RUNNING
            this.execution.setStatus(Execution.Status.RUNNING);
            if (callBack != null) {
                callBack.onStatus(this.execution);
            }
            // do run
            doRun();
            // after do run, if the status is still RUNNING, set it to SUCCEEDED
            if (Execution.Status.RUNNING.equals(this.execution.getStatus())) {
                this.execution.setStatus(Execution.Status.SUCCEEDED);
                if (callBack != null) {
                    callBack.onStatus(this.execution);
                }
            }
        } catch (Exception e) {
            this.execution.setStatus(Execution.Status.FAILED);
            if (callBack != null) {
                callBack.onStatus(this.execution);
            }
            this.execution.setException(e);
            if (callBack != null) {
                callBack.onException(this.execution);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to monitor the Execution: " + execution.getName(), t);
        }
    }

    public abstract void doRun() throws Exception;

}