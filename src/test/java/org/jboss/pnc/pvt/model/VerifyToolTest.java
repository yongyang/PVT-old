/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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

package org.jboss.pnc.pvt.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.util.io.IOUtils;
import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases on basic VerifyTool compose.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class VerifyToolTest {

    @Test
    public void testVersionConventionTool() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        VersionConventionVerifyTool tool = new VersionConventionVerifyTool() {

            private static final long serialVersionUID = 1L;

            @Override
            public Verification verify(VerifyParameter param) {
                final String name = "Version-Check-Test-" + param.getRelease().getName();
                final ExecutionRunnable run = verifyRunnable(param);
                final Execution execution = Execution.createJVMExecution(name, run);
                final Verification verification = new Verification();
                final CallBack callBack = new CallBack() {
                    @Override
                    public void onTerminated(Execution execution) {
                        latch.countDown();
                    }
                    @Override
                    public void onStatus(Execution execution) {
                        verification.syncStauts();
                    }
                    
                    @Override
                    public void onException(Execution execution) {
                        execution.getException().printStackTrace();
                    }
                };
                
                verification.setReleaseId(param.getRelease().getId());
                if (param.getReferenceRelease() != null) {
                    verification.setReferenceReleaseId(param.getReferenceRelease().getId());
                }
                verification.setToolId(param.getToolId());
                verification.setExecution(execution);
                try {
                    Executor.getJVMExecutor().execute(execution, callBack);
                } catch (ExecutionException e) {
                }
                return verification;
            }
            
            @Override
            protected String getVerifiedProductName(VerifyParameter param) {
                return "Test-Prd";
            }
        };
        Product prd = new Product();
        prd.setName("Test-Prd");
        
        Release release = new Release();
        release.setName("1.0.0");
        release.setProductId(prd.getId());
        
        URL zipURL = getClass().getClassLoader().getResource("version-test.zip");
        release.setDistributions(zipURL.toString());
        VerifyParameter param = new VerifyParameter("toolId", null, release, null, false);
        Verification verification = tool.verify(param);
        latch.await(600, TimeUnit.SECONDS); // 10 minutes
        Assert.assertEquals(Verification.Status.PASSED, verification.getStatus());
        URL expectedLog = getClass().getClassLoader().getResource("version-test.txt");
        String expected = new String(Files.readAllBytes(Paths.get(expectedLog.toURI())));
        Assert.assertEquals(expected, verification.getExecution().getReport().getMainLog());
    }

    @Test
    public void testScriptPattern() throws Exception {
        ScriptJenkinsVerifyTool tool = new ScriptJenkinsVerifyTool();
        String script = "# download dist-diff tool\n"
                + "wget -O dist-diff.jar http://10.66.79.92/dist-diff2-jar-with-dependencies.jar\n"
                + "# download zip 1 and unzip it\n"
                + "wget -O current.zip ${CURRENT_ZIP_URL}\n"
                + "unzip -q -d current current.zip\n"
                + "# download zip 2 and unzip it\n"
                + "wget -O previous.zip ${REF_ZIP_URL}\n"
                + "unzip -q -d previous previous.zip\n"
                + "java -jar dist-diff.jar -a current -b previous -i\n";
        tool.setScript(script);
        tool.setArchiver("output/*");

        String jenkinsConfigXML = tool.getJenkinsConfigXML();
        URL expectedLog = getClass().getClassLoader().getResource("script_tool.txt");
        String expected = new String(Files.readAllBytes(Paths.get(expectedLog.toURI())));
        Assert.assertEquals(expected, jenkinsConfigXML);
    }
}