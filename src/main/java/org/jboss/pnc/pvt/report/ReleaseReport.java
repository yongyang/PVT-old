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

package org.jboss.pnc.pvt.report;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * ReleaseReport is used for report of release which has multiple zip files and multiple jar files in each zip file.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@JsonAutoDetect
public class ReleaseReport implements PlainReport {

    @XmlAttribute
    private final String product;

    @XmlAttribute
    private final String release;
    
    private String statusMsg;

    private final List<ZipReport> zipReports = Collections.synchronizedList(new ArrayList<>());

    public ReleaseReport(final String product, final String release) {
        super();
        this.product = product;
        this.release = release;
    }

    /**
     * @return the statusMsg
     */
    public String getStatusMsg() {
        return statusMsg;
    }

    /**
     * @param statusMsg the statusMsg to set
     */
    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @return the release
     */
    public String getRelease() {
        return release;
    }

    /**
     * @return the zipReports
     */
    public List<ZipReport> getZipReports() {
        return zipReports;
    }

    public void addZipReport(ZipReport zipReport) {
        zipReports.add(zipReport);
    }

    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.report.PlainReport#overAll()
     */
    @Override
    @JsonProperty
    public boolean overAll() {
        for (ZipReport zipReport: this.zipReports) {
            if (zipReport.overAll() == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toJSONString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate JSON format report.", e);
        }
    }

    @JsonAutoDetect
    public static class ZipReport implements PlainReport {

        @XmlAttribute(name = "zip")
        private final String zip;

        private URL downloadURL;

        private final List<JarReport> jarReports = Collections.synchronizedList(new ArrayList<>());

        public ZipReport(String zipName) {
            super();
            this.zip = zipName;
        }

        /**
         * @return the downloadURL
         */
        public URL getDownloadURL() {
            return downloadURL;
        }

        /**
         * @param downloadURL the downloadURL to set
         */
        public void setDownloadURL(URL downloadURL) {
            this.downloadURL = downloadURL;
        }

        public void addJarReport(JarReport jarReport) {
            jarReports.add(jarReport);
        }

        /**
         * @return the zip
         */
        public String getZip() {
            return zip;
        }

        /**
         * @return the jarReports
         */
        public List<JarReport> getJarReports() {
            return jarReports;
        }

        /* (non-Javadoc)
         * @see org.jboss.pnc.pvt.report.PlainReport#overAll()
         */
        @Override
        @JsonProperty
        public boolean overAll() {
            for (JarReport jarReport: this.jarReports) {
                if (jarReport.overAll() == false) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toJSONString() {
            try {
                return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to generate JSON format report.", e);
            }
        }

    }

    @JsonAutoDetect
    @JsonSubTypes({ @JsonSubTypes.Type(value = JarReport.class) })
    public static abstract class JarReport implements PlainReport {

        @XmlAttribute
        private final String jar;

        public JarReport(String jarName) {
            super();
            this.jar = jarName;
        }

        /**
         * @return the jar
         */
        public String getJar() {
            return jar;
        }

        @Override
        public String toJSONString() {
            try {
                return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to generate JSON format report.", e);
            }
        }

    }

}
