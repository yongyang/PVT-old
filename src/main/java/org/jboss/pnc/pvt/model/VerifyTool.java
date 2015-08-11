package org.jboss.pnc.pvt.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The base verify tool definition.
 * 
 * Each sub class is recommended to have a 'LABEL' class variant to distinguish it from other tools.
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public abstract class VerifyTool implements Serializable {

    private static final long serialVersionUID = -5353557149342108021L;

    /**
     * TestTool UseType
     */
    public static enum UseType {

        /** tool type which is used for static package analysis **/
        STATIC("Static"),

        /** tool type which is used for run-time testing **/
        RUNTIME("Runtime");

        private String name;

        UseType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        private static final Map<String, UseType> MAP;
        static {
            final Map<String, UseType> map = new HashMap<>();
            for (UseType element : values())
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

        public static UseType forName(final String name) {
            final UseType element = MAP.get(name);
            if (element != null) {
                return element;
            }
            throw new IllegalArgumentException("Unknown type: " + name);
        }
    }

    private long id;

    private String name;

    private String description;

    private UseType useType = UseType.STATIC; // static, runtime, default to STATIC

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
    public UseType getUseType() {
        return useType;
    }

    /**
     * @param type the type to set
     */
    public void setUseType(UseType type) {
        this.useType = type;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((useType == null) ? 0 : useType.hashCode());
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
        if (useType != other.useType)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VerifyTool [id=" + id + ", name=" + name + ", useType=" + useType + "]";
    }

    /**
     * Do verify
     *
     * @param param@return
     */
    protected abstract <T> VerifyResult<T> verify(VerifyParameter param);

    /**
     * @return the Label to distinguish the tool
     */
    public abstract String getLabel();

    /**
     * @return the variant used for wicket page
     */
    public abstract String getPageVariant();

    /**
     * Register all sub class of VerifyTool here
     */
    private static final Map<String, Class<? extends VerifyTool>> toolsMap = new HashMap<>();
    static {
        toolsMap.put(JDKCompatibleVerifyTool.LABEL, JDKCompatibleVerifyTool.class);
        toolsMap.put(VersionConventionVerifyTool.LABEL, VersionConventionVerifyTool.class);
        toolsMap.put(SimpleJenkinsVerifyTool.LABEL, SimpleJenkinsVerifyTool.class);
        toolsMap.put(TemplateJenkinsVerifyTool.LABEL, TemplateJenkinsVerifyTool.class);
        toolsMap.put(ScriptJenkinsVerifyTool.LABEL, ScriptJenkinsVerifyTool.class);
    }

    /**
     * @return a Set contains all registered VerificationTool implementations.
     */
    public static Map<String, Class<? extends VerifyTool>> getAllVerifyToolImplCls() {
        return Collections.unmodifiableMap(toolsMap);
    }

    /**
     * Creates a VerifyTool instance according to the tool label.
     * 
     * @param toolLabel usually comes from 'LABEL' class variant of each VerifyTool implementation.
     * @return a VerifyTool instance which has the label specified.
     * @throws RuntimeException if it can't create such a instance for any reason.
     */
    public static VerifyTool createVerifyTool(String toolLabel) {
        if (toolLabel == null) {
            throw new IllegalArgumentException("Can't create the verify tool instance without knowing the type of it.");
        }
        Class<? extends VerifyTool> toolCls = toolsMap.get(toolLabel);
        if (toolCls == null) {
            throw new IllegalArgumentException("Unknown tool label: " + toolLabel);
        }
        try {
            return toolCls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't create the VerifyTool instance.", e);
        }
    }

}
