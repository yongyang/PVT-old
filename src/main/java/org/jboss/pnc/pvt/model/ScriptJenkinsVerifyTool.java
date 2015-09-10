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

package org.jboss.pnc.pvt.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.wicket.util.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.ExecutionVariable;
import org.jboss.pnc.pvt.execution.JenkinsArchiver;
import org.jboss.pnc.pvt.util.StringFormatter;

import com.offbytwo.jenkins.model.ParameterDefinitions;
import com.offbytwo.jenkins.model.ParametersDefinitionProperty;
import com.offbytwo.jenkins.model.StringParameterDefinition;

/**
 *
 * 
 * A verify tool use script as jenkins job parameter, jenkins job will run the the script to do the verify things
 * 
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class ScriptJenkinsVerifyTool extends TemplateJenkinsVerifyTool implements Serializable {

    private static final long serialVersionUID = 3177423973563547951L;

    private static final Logger logger = Logger.getLogger(ScriptJenkinsVerifyTool.class);

    private String script;

    private String archiver;

    private List<String> stringParams = new ArrayList<>();

    public static final String LABEL = "Script Jenkins Tool";

    private static final String SHELL_SCRIPT_JENKINS_TEMPLTE;
    static {
        try (InputStream input = ScriptJenkinsVerifyTool.class.getClassLoader().getResourceAsStream("jenkins_shell.template");) {
            SHELL_SCRIPT_JENKINS_TEMPLTE = IOUtils.toString(input);
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Can't load jenkins shell template.", e);
        }
    }

    /**
     * @return the archiver
     */
    public String getArchiver() {
        return archiver;
    }

    /**
     * @param archiver the archiver to set
     */
    public void setArchiver(String archiver) {
        this.archiver = archiver;
    }

    /**
     * @return the stringParams
     */
    public List<String> getStringParams() {
        return stringParams;
    }

    /**
     * @param stringParams the stringParams to set
     */
    public void setStringParams(List<String> stringParams) {
        this.stringParams = stringParams;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Jenkins Configuration XML will be composed using the value of <code>script</code> in this object.
     */
    public String getJenkinsConfigXML() {
        String localScript = this.script;
        if (localScript == null) {
            localScript = "";
        }
        Map<String, String> variableMap = new HashMap<>();
        variableMap.put("paramProps", getStrParams());
        variableMap.put("jenkinsScript", localScript);
        variableMap.put("jenkinsPublishers", getPublishers());
        return StringFormatter.replaceVariables(SHELL_SCRIPT_JENKINS_TEMPLTE, variableMap);
    }

    private String getPublishers() {
        logger.debug("Compose publishers for jenkins job");
        if (this.archiver != null && this.archiver.trim().length() > 0) {
            JenkinsArchiver jenkinsArchiver = new JenkinsArchiver();
            jenkinsArchiver.setArtifacts(archiver);
            StringWriter sw = new StringWriter();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(JenkinsArchiver.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(jenkinsArchiver, sw);
            } catch (JAXBException e) {
                throw new RuntimeException("Can't marshalle Jenkins Archiver of ScriptJenkinsVerityTool: " + getName(), e);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<publishers>\n  ");
            sb.append(sw.getBuffer());
            sb.append("\n</publishers>");
            return sb.toString();
        }
        return "<publishers/>";
    }

    private String getStrParams() {
        logger.debug("Compose properties for jenkins job");
        if (stringParams != null && stringParams.size() > 0) {
            Set<String> uniqueParams = new HashSet<String>(stringParams);
            ParametersDefinitionProperty paramProp = new ParametersDefinitionProperty();
            ParameterDefinitions pd = new ParameterDefinitions();
            paramProp.setPd(pd);
            for (String param : uniqueParams) {
                ExecutionVariable var = ExecutionVariable.getVariables().get(param);
                String description = "";
                if (var != null) {
                    description = var.getDescription();
                }
                pd.addParam(new StringParameterDefinition(param, description, ""));
            }
            StringWriter sw = new StringWriter();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ParametersDefinitionProperty.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(paramProp, sw);

            } catch (JAXBException e) {
                throw new RuntimeException(
                        "Can't marshalle Jenkins String Parameters of ScriptJenkinsVerityTool: " + getName(), e);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<properties>\n  ");
            sb.append(sw.getBuffer().toString());
            sb.append("\n</properties>");
            return sb.toString();
        }
        return "<properties/>";
    }
}