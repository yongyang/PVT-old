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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution.JVMExecution;
import org.jboss.pnc.pvt.report.Report;

/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
class JVMExecutor extends Executor {

    private static final Logger logger = Logger.getLogger(JVMExecutor.class);

    static JVMExecutor INSTANCE = new JVMExecutor();

    JVMExecutor() {
        super();
    }

    @Override
    public void execute(final Execution execution, final CallBack callBack) throws ExecutionException {
        JVMExecution jvmExec = (JVMExecution) execution;
        jvmExec.setReport(new Report()); // report will be initialized when it is started.
        ExecutionRunnable run = jvmExec.getRunnable();
        run.setCallback(callBack);
        jvmExec.setStatus(Execution.Status.RUNNING);
        if (callBack != null) {
            callBack.onStatus(execution);
        }
        Future<?> future = getRunnableService().submit(run);
        startMonitor(future, execution, run);
    }

    private void startMonitor(final Future<?> future, final Execution execution, ExecutionRunnable run) {
        final Runnable monitor = new Runnable() {
            public void run() {
                try {
                    future.get();
                    run.setTerminated();
                } catch (InterruptedException e) {
                    run.setException(e);
                    run.setStatus(Execution.Status.FAILED);
                    run.setTerminated();
                    logger.error("Runnable is Interrupted.", e);
                } catch (java.util.concurrent.ExecutionException e) {
                    run.setException((Exception)e.getCause());
                    run.setStatus(Execution.Status.FAILED);
                    run.setTerminated();
                    logger.error("Failed to execute the Runnable.", e.getCause());
                }
            }
        };
        getMonitorExecutorService().schedule(monitor, getMonitorInterval(), TimeUnit.SECONDS);
    }
}
