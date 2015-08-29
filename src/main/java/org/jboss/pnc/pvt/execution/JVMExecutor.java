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

import org.jboss.pnc.pvt.execution.Execution.JVMExecution;

/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
class JVMExecutor extends Executor {

    static JVMExecutor INSTANCE = new JVMExecutor();

    JVMExecutor() {
        super();
    }

    @Override
    public void execute(final Execution execution, final CallBack callBack) throws ExecutionException {
        JVMExecution jvmExec = (JVMExecution) execution;
        ExecutionRunnable run = jvmExec.getRunnable();
        run.setCallback(callBack);
        jvmExec.setStatus(Execution.Status.RUNNING);
        if (callBack != null) {
            callBack.onStatus(execution);
        }
        Future<?> future = getRunnableService().submit(run);
        startMonitor(future, execution, callBack);
    }

    private void startMonitor(Future<?> future, Execution execution, CallBack callBack) {
        final Runnable monitor = new Runnable() {
            public void run() {
                if (future.isDone()) {
                    if (callBack != null) {
                        callBack.onTerminated(execution);
                    }
                } else {
                    getMonitorExecutorService().schedule(this, getMonitorInterval(), TimeUnit.SECONDS);
                }
            }
        };
        getMonitorExecutorService().schedule(monitor, getMonitorInterval(), TimeUnit.SECONDS);
    }
}
