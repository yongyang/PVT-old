package org.jboss.pnc.pvt.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.wicket.PVTApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class JSONDataAccessObject extends PVTDataAccessObject {

    private static ObjectMapper mapper = new ObjectMapper();

    private PVTModel pvtModel;

    private static File jsonFile = new File(PVTApplication.get().getServletContext().getRealPath("/data"), "pvt.json");

    public void load() {
        try {
            pvtModel = mapper.readValue(jsonFile, PVTModel.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void persist() {
        try {
            JsonNode node = mapper.valueToTree(pvtModel);
            mapper.writeValue(jsonFile, node);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PVTModel getPvtModel() {
        return pvtModel;
    }

    public Product newProduct(String name) {
        Product product = new Product();
        product.setName(name);
        return product;
    }

}
