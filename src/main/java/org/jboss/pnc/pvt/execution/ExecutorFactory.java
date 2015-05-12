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

import java.util.Properties;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 * 
 * Before we adopt CDI, this is the entrance to get <code>Executor</code>
 * 
 */
public class ExecutorFactory {

    /**
     * Gets a <code>Executor</code> instance.
     * 
     * Each call produces the same instance with default configuration.
     * 
     * @return an instance of a <code>Executor</code>
     */
    public static Executor getExecutor() {
        return JenkinsExecutor.INSTANCE;
    }

    /**
     * Gets a <code>Executor</code> instance.
     * 
     * Each call produces a new instance with specified Jenkins configuration
     * 
     * @return an instance of a <code>Executor</code>
     */
    public static Executor getJenkinsExecutor(Properties executorProps) {
        return new JenkinsExecutor(executorProps);
    }

}