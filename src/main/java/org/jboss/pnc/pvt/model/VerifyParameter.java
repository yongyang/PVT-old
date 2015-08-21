package org.jboss.pnc.pvt.model;

import java.util.Properties;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {
    private String previousRelease;
    private String currentRelease;

    private String[] previousDistributionZips;
    private String[] currentDistributionZips;

    private Properties properties = new Properties();


    public VerifyParameter(String previousRelease, String currentRelease, String[] previousDistributionZips, String[] currentDistributionZips) {
        this(previousRelease, currentRelease, previousDistributionZips, currentDistributionZips, null);
    }

    public VerifyParameter(String previousRelease, String currentRelease, String[] previousDistributionZips, String[] currentDistributionZips, Properties properties) {
        this.previousRelease = previousRelease;
        this.currentRelease = currentRelease;
        this.previousDistributionZips = previousDistributionZips;
        this.currentDistributionZips = currentDistributionZips;
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

    public String[] getPreviousDistributionZips() {
        return previousDistributionZips;
    }

    public String[] getCurrentDistributionZips() {
        return currentDistributionZips;
    }

    public Properties getProperties() {
        return properties;
    }
}
