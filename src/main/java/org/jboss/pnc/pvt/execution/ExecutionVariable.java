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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public final class ExecutionVariable {

    private String name;

    private String description;

    private ExecutionVariable(final String name, final String description) {
        this.name = name;
        this.description = description;
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

    public static final ExecutionVariable PVT_REST_BASE = new ExecutionVariable("PVT_REST_BASE", "The PVT REST api endponts base url.");

    public static final ExecutionVariable CURRENT_PRODUCT_NAME = new ExecutionVariable("CURRENT_PRODUCT_NAME", "The current product name");

    public static final ExecutionVariable CURRENT_PRODUCT_ID = new ExecutionVariable("CURRENT_PRODUCT_ID", "The current product id");
    
    public static final ExecutionVariable CURRENT_RELEASE_ID = new ExecutionVariable("CURRENT_RELEASE_ID", "The current release id");
    
    public static final ExecutionVariable CURRENT_RELEASE_NAME = new ExecutionVariable("CURRENT_RELEASE_NAME", "The current release name");
    
    public static final ExecutionVariable REF_PRODUCT_NAME = new ExecutionVariable("REF_PRODUCT_NAME", "The reference product name");
    
    public static final ExecutionVariable REF_PRODUCT_ID = new ExecutionVariable("REF_PRODUCT_ID", "The reference product id");
    
    public static final ExecutionVariable REF_RELEASE_ID = new ExecutionVariable("REF_RELEASE_ID", "The reference release id");
    
    public static final ExecutionVariable REF_RELEASE_NAME = new ExecutionVariable("REF_RELEASE_NAME", "The reference release name");
    
    public static final ExecutionVariable CURRENT_ZIP_URLS = new ExecutionVariable("CURRENT_ZIP_URLS", "The current zip urls array");
    
    public static final ExecutionVariable REF_ZIP_URLS = new ExecutionVariable("REF_ZIP_URLS", "The reference zip urls array");
    
    public static final ExecutionVariable CURRENT_ZIP_URL = new ExecutionVariable("CURRENT_ZIP_URL", "The first zip url of current release");
    
    public static final ExecutionVariable REF_ZIP_URL = new ExecutionVariable("REF_ZIP_URL", "The firsh zip url of reference release");

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExecutionVariable other = (ExecutionVariable) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExecutionVariable [name=" + name + ", description=" + description + "]";
    }

    private static final Map<String, ExecutionVariable> variables = new HashMap<>();

    public static Map<String, ExecutionVariable> getVariables() {
        return variables;
    }
    
    static {
        Field[] fields = ExecutionVariable.class.getDeclaredFields();
        for (Field field: fields) {
            if (field.getType().equals(ExecutionVariable.class)) {
                try {
                    ExecutionVariable v = (ExecutionVariable)field.get(null);
                    variables.put(v.getName(), v);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(ExecutionVariable.getVariables().toString());
    }
}
