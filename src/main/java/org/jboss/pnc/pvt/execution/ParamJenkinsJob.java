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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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

    private final List<StringParameterDefinition> stringParams;

    public ParamJenkinsJob(String configXml) throws DocumentException {
        this.configXml = configXml;
        reader = new SAXReader();
        doc = reader.read(new StringReader(configXml));
        stringParams = readJobParams();
    }

    private List<StringParameterDefinition> readJobParams() {
        List<Node> nodes = doc.selectNodes("//hudson.model.StringParameterDefinition");
        if (null == nodes || 0 == nodes.size()) {
            return Collections.emptyList();
        }
        List<StringParameterDefinition> params = new ArrayList<>();
        for (Node node : nodes) {
            Element paramEle = (Element) node;
            String name = paramEle.elementText("name");
            String description = paramEle.elementText("description");
            String defaultValue = paramEle.elementText("defaultValue");
            StringParameterDefinition spd = new StringParameterDefinition(name, description, defaultValue);
            params.add(spd);
        }
        return params;
    }

    public List<StringParameterDefinition> getStringParams() {
        return this.stringParams;
    }

    public String asXml() {
        return this.doc.asXML();
    }

    public String getConfigXml() {
        return configXml;
    }

}
