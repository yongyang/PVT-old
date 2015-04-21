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

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 * Keep it ordered
 *
 */
public enum Stage {
    
    /** initial stage **/
    Initial,
    
    /**
     * Submitted for execution, but may not started yet.
     * 
     * This means the real action has been submitted to the execution phase
     * 
     **/
    Submit,
    
    /**
     * Preparation for execution, like check running environment, required libraries.
     * 
     * Preparation stage is done by the execution tools framework, it can jump to <code>Complete</code>
     * stage in case some prerequisites are not satisfied.
     * 
     */
    Prepare,
    
    /**
     * Running means it starts the verification task
     * 
     */
    Running,
    
    /**
     * It can be either Success result or Failure result.
     */
    Complet
}
