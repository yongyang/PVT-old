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

package org.jboss.pnc.pvt.execution;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Jenkins job archiver configuration.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@XmlRootElement(name = "hudson.tasks.ArtifactArchiver")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "artifacts", "excludes", "allowEmptyArchive", "onlyIfSuccessful", "fingerprint", "defaultExcludes" })
public class JenkinsArchiver implements Serializable {

    private static final long serialVersionUID = -8726454665882003839L;

    @XmlElement
    private String artifacts;

    @XmlElement
    private String excludes;

    @XmlElement
    private boolean allowEmptyArchive = true;

    @XmlElement
    private boolean onlyIfSuccessful;

    @XmlElement
    private boolean fingerprint;

    @XmlElement
    private boolean defaultExcludes = true;

    /**
     * @return the artifacts
     */
    public String getArtifacts() {
        return artifacts;
    }

    /**
     * @param artifacts the artifacts to set
     */
    public void setArtifacts(String artifacts) {
        this.artifacts = artifacts;
    }

    /**
     * @return the excludes
     */
    public String getExcludes() {
        return excludes;
    }

    /**
     * @param excludes the excludes to set
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    /**
     * @return the allowEmptyArchive
     */
    public boolean isAllowEmptyArchive() {
        return allowEmptyArchive;
    }

    /**
     * @param allowEmptyArchive the allowEmptyArchive to set
     */
    public void setAllowEmptyArchive(boolean allowEmptyArchive) {
        this.allowEmptyArchive = allowEmptyArchive;
    }

    /**
     * @return the onlyIfSuccessful
     */
    public boolean isOnlyIfSuccessful() {
        return onlyIfSuccessful;
    }

    /**
     * @param onlyIfSuccessful the onlyIfSuccessful to set
     */
    public void setOnlyIfSuccessful(boolean onlyIfSuccessful) {
        this.onlyIfSuccessful = onlyIfSuccessful;
    }

    /**
     * @return the fingerprint
     */
    public boolean isFingerprint() {
        return fingerprint;
    }

    /**
     * @param fingerprint the fingerprint to set
     */
    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * @return the defaultExcludes
     */
    public boolean isDefaultExcludes() {
        return defaultExcludes;
    }

    /**
     * @param defaultExcludes the defaultExcludes to set
     */
    public void setDefaultExcludes(boolean defaultExcludes) {
        this.defaultExcludes = defaultExcludes;
    }
}
