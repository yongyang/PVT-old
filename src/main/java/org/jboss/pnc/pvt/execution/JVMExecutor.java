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

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution.JVMExecution;

/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
class JVMExecutor extends Executor {

    static JVMExecutor INSTANCE = new JVMExecutor();
    
    private static final Logger logger = Logger.getLogger(JVMExecutor.class);
    
    JVMExecutor() {
        super();
    }

    @Override
    public void execute(final Execution execution) throws ExecutionException {
        JVMExecution jvmExec = (JVMExecution)execution;
        Runnable run = jvmExec.getRunnable();
        String name = jvmExec.getName();
        jvmExec.setStatus(Execution.Status.RUNNING);
        Future<?> future = getJVMExecutorService().submit(run);
        try {
            future.get();
            jvmExec.setStatus(Execution.Status.SUCCEEDED);
        } catch (Exception e) {
            jvmExec.setStatus(Execution.Status.FAILED);
            throw new ExecutionException(String.format("Failed to execute Jenkins job: %s", name), e);
        }
    }


}
