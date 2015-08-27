package org.jboss.pnc.pvt.model;

import java.util.Properties;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {

    private String toolId;

    private Release referenceRelease;
    private Release release;

    private Properties properties = new Properties();


    public VerifyParameter(String toolId, Release referenceRelease, Release release) {
        this(toolId, referenceRelease, release, null);
    }

    public VerifyParameter(String toolId,Release referenceRelease, Release release, Properties properties) {
        this.toolId = toolId;
        this.referenceRelease = referenceRelease;
        this.release = release;
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
        }
    }

    public Release getReferenceRelease() {
        return referenceRelease;
    }

    public Release getRelease() {
        return release;
    }

    public String getToolId() {
        return toolId;
    }

    public void addProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public void getProperty(String name) {
        properties.getProperty(name);
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public Properties getProperties() {
        return properties;
    }
}
