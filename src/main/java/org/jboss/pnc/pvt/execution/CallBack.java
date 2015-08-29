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
 * CallBack for Execution updated events.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
public abstract class CallBack {

    /**
     * On Execution.Status changes.
     * 
     * @param execution The Execution
     */
    public void onStatus(Execution execution) {
    };

    /**
     * On Execution log changes.
     * 
     * @param execution The Execution
     */
    public void onLogChanged(Execution execution) {
    };

    /**
     * On Exception occurred.
     * 
     * @param execution The Execution
     */
    public void onException(Execution execution) {
    };

    /**
     * On Execution Terminated.
     *
     * Possible Terminated time may be: Status changed to Failed or Succeeded,
     * or the Thread of running the execution returns.
     * 
     * @param execution The Execution
     */
    public void onTerminated(Execution execution) {
    };

}
