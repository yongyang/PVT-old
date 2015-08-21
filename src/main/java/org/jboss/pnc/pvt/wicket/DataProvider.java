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

package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pnc.pvt.model.JDKCompatibleVerifyTool;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.SimpleJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.TemplateJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.model.VersionConventionVerifyTool;

/**
 * 
 * This is for Demo only purpose to provide faked data.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public final class DataProvider {

    private DataProvider(){}

    //** ==========  Start of faked Data  =============== *//
    private static List<VerifyTool> tools = new ArrayList<VerifyTool>();
    static {

        SimpleJenkinsVerifyTool simpleJenkinsTool = new SimpleJenkinsVerifyTool();
//        simpleJenkinsTool.setId(1L);
        simpleJenkinsTool.setName("Simple Jenkins Tool");
        simpleJenkinsTool.setDescription("Simple jenkins tool which just specify the Jenkins Job ID");
        simpleJenkinsTool.setJobId("testJob");
        tools.add(simpleJenkinsTool);

        JDKCompatibleVerifyTool jdkTool = new JDKCompatibleVerifyTool();
        jdkTool.setName("JDK-1.8-Check");
//        jdkTool.setId(2L);
        jdkTool.setExpectJDKVersion("1.8");
        jdkTool.setDescription("JDK 1.8 compatible verify tool.");
        tools.add(jdkTool);

        TemplateJenkinsVerifyTool templateJenkinsTool = new TemplateJenkinsVerifyTool();
//        templateJenkinsTool.setId(3L);
        templateJenkinsTool.setName("Template Jenkins Tool");
        templateJenkinsTool.setDescription("Template jenkins tool which specify the config.xml to execute");
        templateJenkinsTool.setJenkinsConfigXML("<?xml version='1.0' encoding='UTF-8'?><project><actions/><description>Run ${toolName} verification test on ${projectName}.&lt;br&gt;&#xd;</description></project>");
        tools.add(templateJenkinsTool);

        ScriptJenkinsVerifyTool scriptJenkinsTool = new ScriptJenkinsVerifyTool();
        scriptJenkinsTool.setScript("java -jar tool.jar SecMain ${args1} ${args2}");
//        scriptJenkinsTool.setId(4L);
        scriptJenkinsTool.setName("Script Jenkins Tool");
        scriptJenkinsTool.setDescription("Script Jenkins tool which you can specify the basic shell script only");
        tools.add(scriptJenkinsTool);

        VersionConventionVerifyTool versionVerifyTool = new VersionConventionVerifyTool();
//        versionVerifyTool.setId(5L);
        versionVerifyTool.setDescription("Version Convention Verify Tool");
        versionVerifyTool.setName("VersionConversionVerify");
        tools.add(versionVerifyTool);
    }

    static List<VerifyTool> getAllTools() {
        return tools;
    }
}
