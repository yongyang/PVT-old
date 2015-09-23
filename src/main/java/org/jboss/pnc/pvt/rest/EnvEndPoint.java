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

package org.jboss.pnc.pvt.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.pnc.pvt.execution.ExecutionVariable;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@Path("/env")
public class EnvEndPoint {

    @GET
    @Path("/variables")
    @Produces(MediaType.TEXT_HTML)
    public Response getVariables() {
        StringBuilder sb = new StringBuilder();
        sb.append("<dl>");
        ExecutionVariable.getVariables().forEach((k, v) -> {
            sb.append("<dt>");
            sb.append(k);
            sb.append("</dt>\n");

            sb.append("<dd>");
            sb.append(v.getDescription());
            sb.append("</dd>\n");
        });
        sb.append("</dl>\n");
        return Response.ok(sb.toString()).build();
    }
}
