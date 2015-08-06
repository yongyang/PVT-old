package org.jboss.pnc.pvt.model;

import java.util.Properties;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {
    private String previousRelease;
    private String currentRelease;

    private String previousDistributionZip;
    private String currentDistributionZip;

    private Properties properties = new Properties();


    public VerifyParameter(String previousRelease, String currentRelease, String previousDistributionZip, String currentDistributionZip) {
        this(previousRelease, currentRelease, previousDistributionZip, currentDistributionZip, null);
    }

    public VerifyParameter(String previousRelease, String currentRelease, String previousDistributionZip, String currentDistributionZip, Properties properties) {
        this.previousRelease = previousRelease;
        this.currentRelease = currentRelease;
        this.previousDistributionZip = previousDistributionZip;
        this.currentDistributionZip = currentDistributionZip;
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
        }

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

    public String getPreviousRelease() {
        return previousRelease;
    }

    public String getCurrentRelease() {
        return currentRelease;
    }

    public String getPreviousDistributionZip() {
        return previousDistributionZip;
    }

    public String getCurrentDistributionZip() {
        return currentDistributionZip;
    }

    public Properties getProperties() {
        return properties;
    }
}
