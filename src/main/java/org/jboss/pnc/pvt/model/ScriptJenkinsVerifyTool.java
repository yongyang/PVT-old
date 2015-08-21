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
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.util.io.IOUtils;
import org.jboss.pnc.pvt.util.StringFormatter;

/**
 *
 * 
 * A verify tool use script as jenkins job parameter, jenkins job will run the the script to do the verify things
 * 
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class ScriptJenkinsVerifyTool extends TemplateJenkinsVerifyTool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3177423973563547951L;

    private String script;

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
        if (this.script == null || this.script.trim().length() == 0) {
            return super.getJenkinsConfigXML();
        }
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("script", this.script);
        return StringFormatter.format(SHELL_SCRIPT_JENKINS_TEMPLTE, variableMap);
    }

}
