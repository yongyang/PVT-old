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

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.offbytwo.jenkins.model.ParameterDefinitions;
import com.offbytwo.jenkins.model.ParametersDefinitionProperty;
import com.offbytwo.jenkins.model.StringParameterDefinition;

/**
 * 
 * ParamJenkinsJob defines a parameterized JenkinsJob.
 * 
 * It supports String type Jenkins Job Parameters now.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings("unchecked")
public class ParamJenkinsJob {

    private final String configXml;

    private final Document doc;

    private final SAXReader reader;

    private final List<SerializableStringParam> stringParams;

    private final JenkinsArchiver archiver;

    public ParamJenkinsJob(String configXml) throws DocumentException {
        this.configXml = configXml;
        reader = new SAXReader();
        doc = reader.read(new StringReader(configXml));
        stringParams = readJobParams();
        archiver = readJobArchiver();
    }

    private JenkinsArchiver readJobArchiver() {
        List<Node> nodes = doc.selectNodes("//hudson.tasks.ArtifactArchiver");
        if (null == nodes || 0 == nodes.size()) {
            return null;
        }
        Element ele = (Element) nodes.get(0);
        Element subEle = ele.element("artifacts");
        String artifacts = subEle == null ? "" : subEle.getTextTrim();
        subEle = ele.element("excludes");
        String excludes = subEle == null ? "" : subEle.getTextTrim();
        subEle = ele.element("allowEmptyArchive");
        boolean allowEmptyArchive = subEle == null ? true : Boolean.valueOf(subEle.getTextTrim());
        subEle = ele.element("onlyIfSuccessful");
        boolean onlyIfSuccessful = subEle == null ? false : Boolean.valueOf(subEle.getTextTrim());
        subEle = ele.element("fingerprint");
        boolean fingerprint = subEle == null ? false : Boolean.valueOf(subEle.getTextTrim());
        subEle = ele.element("defaultExcludes");
        boolean defaultExcludes = subEle == null ? true : Boolean.valueOf(subEle.getTextTrim());
        JenkinsArchiver jar = new JenkinsArchiver();
        jar.setAllowEmptyArchive(allowEmptyArchive);
        jar.setArtifacts(artifacts);
        jar.setDefaultExcludes(defaultExcludes);
        jar.setExcludes(excludes);
        jar.setFingerprint(fingerprint);
        jar.setOnlyIfSuccessful(onlyIfSuccessful);
        return jar;
    }

    private List<SerializableStringParam> readJobParams() {
        List<Node> nodes = doc.selectNodes("//hudson.model.StringParameterDefinition");
        List<SerializableStringParam> params = new ArrayList<>();
        if (null == nodes || 0 == nodes.size()) {
            return params;
        }

        for (Node node : nodes) {
            Element paramEle = (Element) node;
            String name = paramEle.elementText("name");
            String description = paramEle.elementText("description");
            String defaultValue = paramEle.elementText("defaultValue");
            StringParameterDefinition spd = new StringParameterDefinition(name, description, defaultValue);
            params.add(new SerializableStringParam(spd));
        }
        return params;
    }

    public List<SerializableStringParam> getStringParams() {
        return Collections.unmodifiableList(this.stringParams);
    }

    public ParamJenkinsJob addStringParam(String name, String desc, String defaultValue) throws JAXBException,
            DocumentException {
        if (stringParams.stream().anyMatch(p -> p.name.equals(name))) {
            return this; // string parameter with same name has been added already.
        }
        StringParameterDefinition spd = new StringParameterDefinition(name, desc, defaultValue);
        List<Node> nodes = doc.selectNodes("//hudson.model.ParametersDefinitionProperty");
        StringWriter sw = new StringWriter();
        if (null == nodes || 0 == nodes.size()) {
            ParameterDefinitions pd = new ParameterDefinitions();
            pd.addParam(spd);
            ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(pd);
            JAXBContext jaxbContext = JAXBContext.newInstance(ParametersDefinitionProperty.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(pdp, sw);
            Document docInterlude = reader.read(new StringReader(sw.toString()));
            List<Node> propertiesNode = doc.selectNodes("//project/properties");
            for (Node node : propertiesNode) {
                if (node instanceof Element) {
                    Element e = (Element) node;
                    e.add(docInterlude.getRootElement());
                }
            }
        } else {
            JAXBContext jaxbContext = JAXBContext.newInstance(StringParameterDefinition.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(spd, sw);
            Document docInterlude = reader.read(new StringReader(sw.toString()));
            List<Node> propertiesNode = doc.selectNodes("//parameterDefinitions");
            for (Node node : propertiesNode) {
                if (node instanceof Element) {
                    Element e = (Element) node;
                    e.add(docInterlude.getRootElement());
                }
            }
        }
        this.stringParams.add(new SerializableStringParam(spd));
        return this;

    }

    public String asXml() {
        return this.doc.asXML();
    }

    public String getConfigXml() {
        return configXml;
    }

    public String getArchiver() {
        if (this.archiver != null) {
            return this.archiver.getArtifacts();
        }
        return null;
    }

    public static class SerializableStringParam implements Serializable {

        private static final long serialVersionUID = -6511932106227048197L;

        private String name;

        private String description;

        private String defaultValue;

        public SerializableStringParam() {
        }

        private SerializableStringParam(StringParameterDefinition spd) {
            super();
            if (spd == null) {
                throw new IllegalArgumentException("StringParameterDefinition is null.");
            }
            this.name = spd.getName();
            this.description = spd.getDescription();
            this.defaultValue = spd.getDefaultValue();
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the defaultValue
         */
        public String getDefaultValue() {
            return defaultValue;
        }

        /**
         * @param defaultValue the defaultValue to set
         */
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

}
