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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * This is for Demo only purpose to provide faked data.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public class DataProvider {

    private static DataProvider INSTANCE = new DataProvider();
    private DataProvider(){}

    public static DataProvider INSTANCE() {
        return INSTANCE;
    }

    //** ==========  Start of faked Data  =============== *//
    private static List<Product> products = new ArrayList<Product>();
/*
    static {
        Product prd = new Product();
        prd.setId(1L);
        prd.setName("EAP");
        prd.setDescription("6.4.x");
        prd.setMavenRepo("http://download.devel.redhat.com/devel/candidates/JBEAP/");
        products.add(prd);

        prd = new Product();
        prd.setId(2L);
        prd.setName("EAP");
        prd.setDescription("7.0.x");
        prd.setMavenRepo("http://download.devel.redhat.com/devel/candidates/JBEAP/");
        products.add(prd);

        prd = new Product();
        prd.setId(3L);
        prd.setName("JWS");
        prd.setDescription("3.0.x");
        prd.setMavenRepo("http://download.devel.redhat.com/devel/candidates/JWS/");
        products.add(prd);

        prd = new Product();
        prd.setId(4L);
        prd.setName("BRMS");
        prd.setDescription("6.1.x");
        prd.setMavenRepo("http://download.devel.redhat.com/devel/candidates/BRMS/");
        products.add(prd);

        prd = new Product();
        prd.setId(5L);
        prd.setName("JBoss Fuse");
        prd.setDescription("6.1.x");
        prd.setMavenRepo("http://download.devel.redhat.com/devel/candidates/fuse/");
        products.add(prd);
    }
*/

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getProductsByName(String name) {
        return products.stream().filter(p -> name.equals(p.getName())).collect(Collectors.toList());
    }

}
