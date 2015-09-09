package org.jboss.pnc.pvt.model;

import java.util.Properties;

import org.jboss.pnc.pvt.execution.ExecutionVariable;
import org.jboss.pnc.pvt.wicket.PVTApplication;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {

    /** Property key which indicates whether skip the passed verification or not. Default false **/
    public static final String SKIP_PASSED = "skip.passed";
    
    private String toolId;

    private Release referenceRelease;
    private Release release;

    private Properties properties = new Properties();


    public VerifyParameter(String toolId, Release referenceRelease, Release release) {
        this(toolId, referenceRelease, release, null);
    }

    public VerifyParameter(String toolId,Release referenceRelease, Release release, Properties properties) {
        this(toolId, referenceRelease, release, properties, true);
    }

    public VerifyParameter(String toolId,Release referenceRelease, Release release, Properties properties, boolean prefillVars) {
        this.toolId = toolId;
        this.referenceRelease = referenceRelease;
        this.release = release;
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
        }
        if (prefillVars) {
            prefillInternalVariables();
        }
    }
    
    private void prefillInternalVariables() {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        if (this.release != null) {
            addProperty(ExecutionVariable.CURRENT_RELEASE_ID.getName(), this.release.getId());
            addProperty(ExecutionVariable.CURRENT_RELEASE_NAME.getName(), this.release.getName());
            String productId = this.release.getProductId();
            Product product = pvtModel.getProductById(productId);
            addProperty(ExecutionVariable.CURRENT_PRODUCT_ID.getName(), productId);
            addProperty(ExecutionVariable.CURRENT_PRODUCT_NAME.getName(), product.getName());
            String[] zipUrls = this.release.getDistributionArray();
            if (zipUrls != null && zipUrls.length > 0) {
                // choose the first one as the main url
                addProperty(ExecutionVariable.CURRENT_ZIP_URL.getName(), zipUrls[0]);
                addProperty(ExecutionVariable.CURRENT_ZIP_URLS.getName(), String.join(" ", zipUrls));
            }
        }
        if (this.referenceRelease != null) {
            addProperty(ExecutionVariable.REF_RELEASE_ID.getName(), this.referenceRelease.getId());
            addProperty(ExecutionVariable.REF_RELEASE_NAME.getName(), this.referenceRelease.getName());
            String productId = this.referenceRelease.getProductId();
            Product product = pvtModel.getProductById(productId);
            addProperty(ExecutionVariable.REF_PRODUCT_ID.getName(), productId);
            addProperty(ExecutionVariable.REF_PRODUCT_NAME.getName(), product.getName());
            String[] zipUrls = this.referenceRelease.getDistributionArray();
            if (zipUrls != null && zipUrls.length > 0) {
                // choose the first one as the main url
                addProperty(ExecutionVariable.REF_ZIP_URL.getName(), zipUrls[0]);
                addProperty(ExecutionVariable.REF_ZIP_URLS.getName(), String.join(" ", zipUrls));
            }
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

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public Properties getProperties() {
        return properties;
    }
}
