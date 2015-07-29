/*
 * JBoss, Home of Professional Open Source
 * Copyright @year, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jboss.pnc.pvt.config.PVTStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <code>Product</code> represents a product stream, like: EAP 6.4.x, BRMS 6.0.x, etc.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@JsonAutoDetect
public class Product implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5742290247172000842L;

    // uuid
    private String id = UUID.randomUUID().toString();
    
    private String name;

    private String packages;

    // package maintainer
    private String maintainer;

    // developer
    private String developer;

    private String qe;
    
    private String description;

    private List<Release> releases = new ArrayList<>();


    public Product() {
    }

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

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
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

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getQe() {
        return qe;
    }

    public void setQe(String qe) {
        this.qe = qe;
    }

    /* (non-Javadoc)
             * @see java.lang.Object#hashCode()
             */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        Product other = (Product) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
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
        return "Product [name=" + name + ", description=" + description + "]";
    }

    /**
     * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
     */
    @JsonAutoDetect
    public static class Release {

        private String id = UUID.randomUUID().toString();

        // ref of product
        private String productId;

        // ex: 7.0.0.DR1
        private String release;

        // the distribution zips, urls to download,
        // ex: http://download.devel.redhat.com/devel/candidates/JBEAP/JBEAP-7.0.0.DR6/jboss-eap-7.0.0.DR6.zip
        private List<String> distributions = new ArrayList<>();

        private PVTStatus status;


    }
}
