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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.pnc.pvt.util.StringFormatter;

/**
 * 
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
abstract class JobMapper {

    static JobMapper DEFAULT = new DefaultJobMapper();

    /**
     * Gets the job configuration according to the tool name.
     * 
     * @param toolName the tool name
     * @return the job content, null if no mapping found
     */
    abstract String getJobXMLContent(String toolName);
    /**
     * 
     * @param productName the product name which the job will be executed for.
     * @param version the product version
     * @param toolName the concrete tool name.
     * @return the jenkins job name
     */
    String getJobName(String productName, String version, String toolName) {
        StringBuilder sb = new StringBuilder();
        sb.append(productName);
        sb.append("-");
        sb.append(version);
        sb.append("-");
        sb.append(toolName);
        return sb.toString();
    }
    
    /*Load default jenkins.template*/
    public static class DefaultJobMapper extends JobMapper {

        @Override
        String getJobXMLContent(String toolName) {
            InputStream in = getClass().getResourceAsStream("/jenkins.template");
            StringWriter writer = new StringWriter();
            try {
				IOUtils.copy(in, writer, "UTF8");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
            String theContent=writer.toString();
            Map<String, Object> variableMap=new HashMap<String, Object>();
            InputStream in_runsh = getClass().getResourceAsStream("/testtool/"+toolName+"/run.sh");
            InputStream in_runprop = getClass().getResourceAsStream("/testtool/"+toolName+"/run.properties");
            StringWriter writer_runsh = new StringWriter();
            StringWriter writer_runprop = new StringWriter();
            try {
            	IOUtils.copy(in_runsh, writer_runsh, "UTF8");
            	IOUtils.copy(in_runprop, writer_runprop, "UTF8");
            } catch (IOException e1) {
            	// TODO Auto-generated catch block
            	e1.printStackTrace();
            	return null;
            }
            variableMap.put("toolName", toolName);
            variableMap.put("run.sh", writer_runsh.toString());
            variableMap.put("run.properties", writer_runprop.toString());
        	
        	return StringFormatter.format(theContent, variableMap);
        }
        

    }
    
}

