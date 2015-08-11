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

import org.jboss.pnc.pvt.model.VerifyTool.UseType;
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
    public void testCreateInstance() {
        VerifyTool tool = VerifyTool.createVerifyTool(JDKCompatibleVerifyTool.LABEL);
        Assert.assertNotNull(tool);
        Assert.assertEquals(JDKCompatibleVerifyTool.class, tool.getClass());
        Assert.assertEquals(UseType.STATIC, tool.getUseType());
        tool.setUseType(UseType.RUNTIME);
        Assert.assertEquals(UseType.STATIC, tool.getUseType()); // still static

        tool = VerifyTool.createVerifyTool(VersionConventionVerifyTool.LABEL);
        Assert.assertNotNull(tool);
        Assert.assertEquals(VersionConventionVerifyTool.class, tool.getClass());
        Assert.assertEquals(UseType.STATIC, tool.getUseType());
        tool.setUseType(UseType.RUNTIME);
        Assert.assertEquals(UseType.STATIC, tool.getUseType()); // still static

        tool = VerifyTool.createVerifyTool(SimpleJenkinsVerifyTool.LABEL);
        Assert.assertNotNull(tool);
        Assert.assertEquals(SimpleJenkinsVerifyTool.class, tool.getClass());
        Assert.assertEquals(UseType.STATIC, tool.getUseType()); // default to static
        tool.setUseType(UseType.RUNTIME);
        Assert.assertEquals(UseType.RUNTIME, tool.getUseType());

        tool = VerifyTool.createVerifyTool(TemplateJenkinsVerifyTool.LABEL);
        Assert.assertNotNull(tool);
        Assert.assertEquals(TemplateJenkinsVerifyTool.class, tool.getClass());

        ScriptJenkinsVerifyTool scriptTool = (ScriptJenkinsVerifyTool)VerifyTool.createVerifyTool(ScriptJenkinsVerifyTool.LABEL);
        Assert.assertNotNull(scriptTool);
        Assert.assertEquals(ScriptJenkinsVerifyTool.class, scriptTool.getClass());
        scriptTool.setScript("java -jar my.jar * hello world!");

        String expectedXML = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<project>\n" +
                "  <actions/>\n" +
                "  <description>Run verification test.&lt;br&gt;&#xd;\n" +
                "</description>\n" +
                "  <logRotator class=\"hudson.tasks.LogRotator\">\n" +
                "    <daysToKeep>-1</daysToKeep>\n" +
                "    <numToKeep>2</numToKeep>\n" +
                "    <artifactDaysToKeep>-1</artifactDaysToKeep>\n" +
                "    <artifactNumToKeep>-1</artifactNumToKeep>\n" +
                "  </logRotator>\n" +
                "  <keepDependencies>false</keepDependencies>\n" +
                "  <assignedNode>jboss-prod-bos</assignedNode>\n" +
                "  <canRoam>false</canRoam>\n" +
                "  <disabled>false</disabled>\n" +
                "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
                "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
                "  <triggers/>\n" +
                "  <concurrentBuild>false</concurrentBuild>\n" +
                "  <builders>\n" +
                "    <hudson.tasks.Shell>\n" +
                "      <command>java -jar my.jar * hello world!</command>\n" +
                "    </hudson.tasks.Shell>\n" +
                "  </builders>\n" +
                "  <publishers>\n" +
                "   <hudson.tasks.ArtifactArchiver>\n" +
                "      <artifacts>archive/**/*</artifacts>\n" +
                "      <latestOnly>true</latestOnly>\n" +
                "      <zip>false</zip>\n" +
                "    </hudson.tasks.ArtifactArchiver>\n" +
                "    {failure_mailer}\n" +
                "  </publishers>\n" +
                "  <buildWrappers>\n" +
                "    <hudson.plugins.timestamper.TimestamperBuildWrapper/>\n" +
                "  </buildWrappers>\n" +
                "</project>";
        Assert.assertEquals(expectedXML, scriptTool.getJenkinsConfigXML());
        
        scriptTool.setJenkinsConfigXML("test not working string");
        Assert.assertEquals(expectedXML, scriptTool.getJenkinsConfigXML()); // set jenkins config xml does not work because it uses the script for the job content
    }
}
