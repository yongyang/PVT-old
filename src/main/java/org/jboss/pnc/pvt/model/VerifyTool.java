package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.*;

/**
 * The base verify tool definition.
 * 
 * Each sub class is recommended to have a 'LABEL' class variant to distinguish it from other tools.
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class VerifyTool<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -5353557149342108021L;

    private String id = UUID.randomUUID().toString() ;

    private String name;

    private String description;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
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
        VerifyTool other = (VerifyTool) obj;
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
        return "VerifyTool [id=" + id + ", name=" + name + "]";
    }

    /**
     * Do verify, normally the worker need to be run in a separate thread
     *
     * @param param @return result, maybe a handle for asynchronous job, such a jenkins job
     */
    public abstract Verification<T> verify(VerifyParameter param);

    /**
     * Register all sub class of VerifyTool here
     */
    private static final Map<String, Class<? extends VerifyTool>> toolsMap = new HashMap<>();
    static {
//        toolsMap.put(JDKCompatibleVerifyTool.LABEL, JDKCompatibleVerifyTool.class);
//        toolsMap.put(VersionConventionVerifyTool.LABEL, VersionConventionVerifyTool.class);
//        toolsMap.put(SimpleJenkinsVerifyTool.LABEL, SimpleJenkinsVerifyTool.class);
//        toolsMap.put(TemplateJenkinsVerifyTool.LABEL, TemplateJenkinsVerifyTool.class);
//        toolsMap.put(ScriptJenkinsVerifyTool.LABEL, ScriptJenkinsVerifyTool.class);
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

    public Verification<T> newDefaultVerification(VerifyParameter param, T resultObject) {
        Verification<T> verification = new Verification<>();
        verification.setReleaseId(param.getRelease().getId());
        if(param.getReferenceRelease() != null) {
            verification.setReferenceReleaseId(param.getReferenceRelease().getId());
        }
        verification.setToolId(param.getToolId());
        verification.setResultObject(resultObject);
        return verification;
    }
}
