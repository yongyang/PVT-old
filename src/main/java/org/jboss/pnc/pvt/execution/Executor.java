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

import java.util.Map;
import java.util.concurrent.Future;


/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 * <code>Executor</code> is responsible to start a verification action 
 *
 * Implementation of this interface should be state-less.
 * 
 */
public interface Executor {

    /**
     * Executes a job by the job name.
     * 
     * Returns immediately after sending execution message.
     * 
     * @param jobName name of the execution job. Globally unique.
     * @return an Execution which contains the execution information.
     * @throws ExecutionException on any exception
     */
    Execution execute(String jobName) throws ExecutionException;
    
    /**
     * Executes a job by the job name.
     * 
     * Returns the Future instance immediately. This can be used by waiting on a Stage on.
     * 
     * @param jobName the job name. Globally unique.
     * @param stage which stage this should wait for.
     * @return A Future instance with Execution information
     * @throws ExecutionException on any exception
     */
    Future<Execution> execute(String jobName, Stage stage) throws ExecutionException;

    /**
     * Executes a job by the job name.
     * 
     * Returns immediately after sending execution message.
     * 
     * @param jobName name of the execution job. Globally unique.
     * @param params Parameters send to the job
     * @return an Execution which contains the execution information.
     * @throws ExecutionException on any exception
     */
    Execution execute(String jobName, Map<String, String> params) throws ExecutionException;
    
    /**
     * Executes a job by the job name.
     * 
     * Returns the Future instance immediately. This can be used by waiting on a Stage on.
     * 
     * @param jobName the job name. Globally unique.
     * @param params Parameters send to the job
     * @param stage which stage this should wait for.
     * @return A Future instance with Execution information
     * @throws ExecutionException on any exception
     */
    Future<Execution> execute(String jobName, Map<String, String> params, Stage stage) throws ExecutionException;
}
