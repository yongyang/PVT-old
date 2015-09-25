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

package org.jboss.pnc.pvt.util;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public final class PVTEnvrionment {

    private PVTEnvrionment() {
    }

    private static String ctxPath = "/pvt";

    public static String getHttpURLBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        String host = System.getProperty("pvt.http.host", System.getProperty("jboss.bind.address", "localhost"));

        sb.append(host);
        String port = System.getProperty("pvt.http.port", "8080");
        if (!"80".equals(port)) {
            sb.append(":");
            sb.append(port);
        }
        return sb.toString();
    }

    public static String getRESTURLBase() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHttpURLBase());
        sb.append(ctxPath);
        return sb.toString();
    }

    public static void setCtxPath(String ctxPath2) {
        ctxPath = ctxPath2;
    }
}
