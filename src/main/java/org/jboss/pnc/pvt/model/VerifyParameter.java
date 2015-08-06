package org.jboss.pnc.pvt.model;

import java.util.Properties;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {
    private String previousRelease;
    private String currentRelease;

    private Properties properties = new Properties();

    public VerifyParameter(String previousRelease, String currentRelease) {
        this.previousRelease = previousRelease;
        this.currentRelease = currentRelease;
    }

    public VerifyParameter(String previousRelease, String currentRelease, Properties properties) {
        this.previousRelease = previousRelease;
        this.currentRelease = currentRelease;
        this.properties = properties;
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


}
