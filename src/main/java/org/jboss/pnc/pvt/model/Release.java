package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
public class Release implements Serializable {

    private String id = UUID.randomUUID().toString();

    // id of product
    private String productId;

    // release name ex: 7.0.0.DR1
    private String name;

    // the distribution zips, urls to download, separate by space
    // ex: http://download.devel.redhat.com/devel/candidates/JBEAP/JBEAP-7.0.0.DR6/jboss-eap-7.0.0.DR6.zip
    private String distributions;

    // The tools applied to this release
    private List<String> tools = new ArrayList<>();

    // Runtime verification, toolId => verificationId
    private Map<String, String> verifications = new HashMap<>();

    private String description;

    private PVTStatus status = PVTStatus.NEW;

    private long createTime = System.currentTimeMillis();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistributions() {
        return distributions;
    }

    @JsonIgnore
    public String[] getDistributionArray(){
        return distributions.split("\\\r\\n");
    }

    public void setDistributions(String distributions) {
        this.distributions = distributions;
    }

    public PVTStatus getStatus() {
        return status;
    }

    public void setStatus(PVTStatus status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }
}
