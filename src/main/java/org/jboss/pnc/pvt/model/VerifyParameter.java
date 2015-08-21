package org.jboss.pnc.pvt.model;

import java.util.Properties;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {

    private Release previousRelease;
    private Release currentRelease;

    private Properties properties = new Properties();


    public VerifyParameter(Release previousRelease, Release currentRelease) {
        this(previousRelease, currentRelease, null);
    }

    public VerifyParameter(Release previousRelease, Release currentRelease, Properties properties) {
        this.previousRelease = previousRelease;
        this.currentRelease = currentRelease;
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
        }
    }

    public Release getPreviousRelease() {
        return previousRelease;
    }

    public Release getCurrentRelease() {
        return currentRelease;
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
