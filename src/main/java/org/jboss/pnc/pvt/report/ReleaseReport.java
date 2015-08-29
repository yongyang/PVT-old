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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@JsonAutoDetect
@JsonSubTypes({@JsonSubTypes.Type(value = ReleaseReport.class)})
public class ReleaseReport implements Serializable {

    private static final long serialVersionUID = 2763849809344683704L;

    private final String product;
    private final String release;

    public ReleaseReport(final String product, final String release) {
        super();
        this.product = product;
        this.release = release;
    }

    private List<JarReport> zipReports = new ArrayList<>();

    public void addZipReport(String zip) {
        
    }

    public abstract class JarReport implements Serializable {

        private static final long serialVersionUID = -5457492260651931487L;

        private final String jar;

        public JarReport(String jarName) {
            super();
            this.jar = jarName;
        }

    }

}
