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

package org.jboss.pnc.pvt.rest.endpoints;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

/**
 * 
 * This is a Test Servlet to just print out all headers/params out. 
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@WebServlet("/echo")
public class TestSerlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(TestSerlet.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("================================<br />\n");
        sb.append("Http Method: " + request.getMethod() + "<br/>\n");
        sb.append("================================<br />\n");
        
        sb.append("<hr>\n");

        sb.append("================================<br />\n");
        sb.append("Http Headers:<br/><ul>\n");

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            sb.append("<li>");
            sb.append(header + " := ");
            sb.append(joinEnums(request.getHeaders(header)));
            sb.append("</li>\n");
        }
        sb.append("</ul>================================<br />\n");

        sb.append("<hr>\n");

        sb.append("================================<br />\n");
        sb.append("Http Parameters:<br/><ul>\n");

        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            sb.append("<li>");
            sb.append(param + " := ");
            sb.append(String.join(" | ", request.getParameterValues(param)));
            sb.append("</li>\n");
        }
        sb.append("</ul>================================<br />\n");

        // log to console
        logger.info(sb.toString());

        // print to http client
        PrintWriter out = response.getWriter();
        out.println(sb.toString());
        out.flush();
    }

    private String joinEnums(Enumeration<String> strs) {
        StringBuilder sb = new StringBuilder();
        while (strs.hasMoreElements()) {
            sb.append(strs.nextElement());
            sb.append(" | ");
        }
        return sb.toString();
    }

}
