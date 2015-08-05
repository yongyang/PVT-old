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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <code> TestTool </code> The test tool name and script,
 * 
 * like: tool name is zip-diff and command is https//code.engineering.redhat.com/pvt-test-tools/run-zip-diff.sh .
 * 
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class TestTool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3177423973563547951L;

	/**
	 * TestTool Type
	 */
	public static enum Type {
	    
	    /** tool type which is used for static package analysis **/
	    STATIC("Static"),

	    /** tool type which is used for run-time testing **/
	    RUNTIME("Runtime");

	    private String name;

	    Type(final String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	        return this.name;
	    }

	    public String getName() {
	        return this.name;
	    }

	    private static final Map<String, Type> MAP;
	    static {
	        final Map<String, Type> map = new HashMap<String, Type>();
	         for (Type element : values())
	         {
	            final String name = element.getName();
	            if (name != null)
	               map.put(name, element);
	         }
	         MAP = map;
	    }

	    public static Set<String> names() {
	        return MAP.keySet();
	    }

        public static List<String> namesList() {
            List<String> list = new ArrayList<String>();
            list.addAll(names());
            return list;
        }

	    public static Type forName(final String name) {
	        final Type element = MAP.get(name);
	        if (element != null) {
	            return element;
	        }
	        throw new IllegalArgumentException("Unknown type: " + name);
	    }
	}

	 /**
     * TestTool Level
     */
    public static enum Level {
        
        /** tool level which is suit for all levels checking **/
        ALL("All"),

        /** tool level which is suit for package level checking only **/
        PACKAGE("Package"),
        
        /** tool level which is suit for product level checking only **/
        PRODUCT("Product");

        private String name;

        Level(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public static List<String> namesList() {
            List<String> list = new ArrayList<String>();
            list.addAll(names());
            return list;
        }

        private static final Map<String, Level> MAP;
        static {
            final Map<String, Level> map = new HashMap<String, Level>();
             for (Level element : values())
             {
                final String name = element.getName();
                if (name != null)
                   map.put(name, element);
             }
             MAP = map;
        }


        public static Set<String> names() {
            return MAP.keySet();
        }

        public static Level forName(final String name) {
            final Level element = MAP.get(name);
            if (element != null) {
                return element;
            }
            throw new IllegalArgumentException("Unknown level: " + name);
        }
    }

	private long id;
    
    private String name;
    
    private String description;

    private Type type; // static, runtime

    private Level level; // all, package, product

    private String command;

    /**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
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
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((command == null) ? 0 : command.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        TestTool other = (TestTool) obj;
        if (command == null) {
            if (other.command != null)
                return false;
        } else if (!command.equals(other.command))
            return false;
        if (level != other.level)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestTool [id=" + id + ", name=" + name + ", type=" + type + ", level=" + level + "]";
    }

}
