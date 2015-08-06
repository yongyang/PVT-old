package org.jboss.pnc.pvt.model;

import java.util.*;

/**
 * The base verity tool definition
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public abstract class VerifyTool {
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

    private long id;

    private String name;

    private String description;

    private Type type; // static, runtime

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



    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        VerifyTool other = (VerifyTool) obj;
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
        return "VerifyTool [id=" + id + ", name=" + name + ", type=" + type + "]";
    }


    /**
     * Do verify
     *
     * @param param@return
     */
    protected abstract VerifyResult verify(VerifyParameter param);
}
