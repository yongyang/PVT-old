package org.jboss.pnc.pvt.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.core.Application;

import com.wordnik.swagger.jaxrs.config.BeanConfig;

public class PVTJaxRSActivator extends Application {
    
    public PVTJaxRSActivator() {
        super();
        try {
            Properties props = getSwaggerProperties();
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion(props.getProperty("apiVersion"));
            beanConfig.setTitle(props.getProperty("title"));
            beanConfig.setDescription(props.getProperty("description"));
            beanConfig.setBasePath("/pvt/rest");
            beanConfig.setPrettyPrint(true);
            beanConfig.setResourcePackage(props.getProperty("resourcepackages"));
            beanConfig.setScan(true);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Properties getSwaggerProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/swagger.properties")) {
            props.load(input);
        }
        return props;
    }

}
