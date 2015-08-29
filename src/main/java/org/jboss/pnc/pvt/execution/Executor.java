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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <code>Executor</code> is responsible to start a execution.
 * 
 * It does not care about which product/version the execution is for.
 *
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
public abstract class Executor {

    /** System property key for thread pool size for PVT to check status of a running execution. **/
    public static final String KEY_PVT_CHECK_POOL_SIZE = "pvt.check.pool.size";

    /** System property key for thread pool size for PVT to run a execution inside current JVM. **/
    public static final String KEY_PVT_JVM_POOL_SIZE = "pvt.jvm.pool.size";

    /** System property key for max retry times on IOException **/
    public static final String KEY_PVT_MAX_RETRY = "pvt.max.retry.time";

    /** System property key for interval on monitoring the worker thread **/
    public static final String KEY_PVT_MONITOR_INTERVAL = "pvt.monitor.interval";

    /**
     * Starts an execution either to a Jenkins server or running inside the JVM.
     * 
     * Returns immediately after sending execution message.
     * 
     * @param execution the execution.
     * @param callBack the CallBack used to trigger status change events.
     * @throws ExecutionException on any exception
     */
    public abstract void execute(Execution execution, CallBack callBack) throws ExecutionException;

    /**
     * @return the ScheduledExecutorService used to monitor an Execution
     */
    ScheduledExecutorService getMonitorExecutorService() {
        return MONITOR_EXESERVICE;
    }

    /**
     * @return the Thread Pool Size. Default to current available processors count * 2.
     */
    static int getMonitorThreadPoolSize() {
        return Integer.getInteger(KEY_PVT_CHECK_POOL_SIZE, Runtime.getRuntime().availableProcessors() * 2);
    }

    int getMaxRetryTime() {
        return Integer.getInteger(KEY_PVT_MAX_RETRY, 5);
    }

    int getMonitorInterval() {
        return Integer.getInteger(KEY_PVT_MONITOR_INTERVAL, 10);
    }

    /**
     * @return the ExecutorService used to run the JVM execution
     */
    ExecutorService getRunnableService() {
        return RUNNABLE_EXESERVICE;
    }

    /**
     * @return Default Jenkins Property.
     * 
     * @throws IOException
     */
    public static JenkinsConfiguration getDefaultJenkinsProps() throws IOException {
        Properties props = new Properties();
        try (InputStream input = Executor.class.getResourceAsStream("/jenkins.properties")) {
            props.load(input);
        }
        return JenkinsConfiguration.fromProperty(props);
    }

    private static ScheduledExecutorService MONITOR_EXESERVICE = Executors.newScheduledThreadPool(getMonitorThreadPoolSize(),
            new ThreadFactory() {

                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "PVT-Monitor-" + threadNumber.getAndIncrement());
                    if (t.isDaemon())
                        t.setDaemon(false);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

    private static ExecutorService RUNNABLE_EXESERVICE = Executors.newCachedThreadPool(new ThreadFactory() {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "PVT-RUNNABLE-" + threadNumber.getAndIncrement());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    });

    /**
     * Gets a Jenkins <code>Executor</code> instance.
     * 
     * Each call produces the same instance with default configuration.
     * 
     * The default jenkins configuration comes from 'jenkins.properties' in classpath.
     * 
     * @return an instance of a Jenkins <code>Executor</code>
     */
    public static Executor getJenkinsExecutor() {
        return JenkinsExecutor.INSTANCE;
    }

    /**
     * Gets a Jenkins <code>Executor</code> instance with a specified JenkinsConfiguration.
     * 
     * Each call produces a new instance with specified Jenkins configuration
     * 
     * @return an instance of a <code>Executor</code>
     */
    public static Executor getJenkinsExecutor(JenkinsConfiguration jenkinsConfig) {
        return new JenkinsExecutor(jenkinsConfig);
    }

    /**
     * Gets a JVM <code>Executor</code> instance.
     * 
     * This type of Executor will execute inside the JVM of PVT instance, it is not recommended though.
     * 
     * @return a JVM <code>Executor</code> instance
     */
    public static Executor getJVMExecutor() {
        return JVMExecutor.INSTANCE;
    }

}
