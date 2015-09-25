package org.jboss.pnc.pvt.model;

import java.util.Objects;
import java.util.Properties;

import org.jboss.pnc.pvt.execution.ExecutionVariable;
import org.jboss.pnc.pvt.util.PVTEnvrionment;
import org.jboss.pnc.pvt.wicket.PVTApplication;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VerifyParameter {

    /** Property key which indicates whether skip the passed verification or not. Default false **/
    public static final String SKIP_PASSED = "skip.passed";
    
    private final String toolId;

    private final Release referenceRelease;
    private final Release release;

    private final Properties properties = new Properties();

    public VerifyParameter(String toolId, Release release) {
        this(toolId, null, release, null);
    }

    public VerifyParameter(String toolId, Release referenceRelease, Release release) {
        this(toolId, referenceRelease, release, null);
    }

    public VerifyParameter(String toolId,Release referenceRelease, Release release, Properties properties) {
        this(toolId, referenceRelease, release, properties, true);
    }

    public VerifyParameter(String toolId,Release referenceRelease, Release release, Properties properties, boolean prefillVars) {
        Objects.requireNonNull(toolId, "toolId can't be null");
        Objects.requireNonNull(release, "release can't be null");
        this.toolId = toolId;
        this.release = release;
        if (referenceRelease == null) {
            String refId = release.getReferenceReleaseId();
            if (refId != null) {
                this.referenceRelease = PVTApplication.getDAO().getPvtModel().getReleasebyId(refId);
            } else {
                this.referenceRelease = null;
            }
        } else {
            this.referenceRelease = referenceRelease;
        }
        if(properties != null && !properties.isEmpty()) {
            this.properties.putAll(properties);
        }
        if (prefillVars) {
            prefillInternalVariables();
        }
    }
    
    private void prefillInternalVariables() {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        addProperty(ExecutionVariable.PVT_REST_BASE.getName(), PVTEnvrionment.getRESTURLBase());
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
            String mvnRepo = this.release.getRepo();
            if (mvnRepo != null && mvnRepo.trim().length() > 0) {
                addProperty(ExecutionVariable.CURRENT_MVN_REPO_URL.getName(), mvnRepo);
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
            String mvnRepo = this.referenceRelease.getRepo();
            if (mvnRepo != null && mvnRepo.trim().length() > 0) {
                addProperty(ExecutionVariable.REF_MVN_REPO_URL.getName(), mvnRepo);
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
