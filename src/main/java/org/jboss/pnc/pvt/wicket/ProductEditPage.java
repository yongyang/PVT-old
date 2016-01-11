package org.jboss.pnc.pvt.wicket;


import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class ProductEditPage extends ProductNewPage {
	
    public ProductEditPage(PageParameters pp) {
        this(pp, "PVT product is to be modified.");
    }
    
    public ProductEditPage(PageParameters pp, String info) {
    	super(pp, info);
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        removeButton.setVisible(true);
    }
    
    @Override
    public String getTitle() {
        return "Modify a Prodcut";
    }
	
    @Override
    protected Product getProduct(PageParameters pp) {
        if (pp != null) {
            if (!pp.get("productId").isNull()) {
                PVTDataAccessObject dao = PVTApplication.getDAO();
                return dao.getPvtModel().getProductById(pp.get("productId").toString());
            }
        }
        return null;
    }
    
    @Override
    public void doSubmit() {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().updateProduct(product);
        product.setTools(toolCheckBoxMultipleChoice.getInputAsArray() != null ? Arrays.asList(toolCheckBoxMultipleChoice.getInputAsArray()) : Collections.emptyList());
        dao.persist();
        setInfo("Product: " + product.getName() + " is Updated.");
    }
    
    @Override
    public void doReset() {
    	productForm.getModel().setObject(product);
    }
    
    @Override
    public boolean doRemove() {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        boolean success = dao.getPvtModel().removeProduct(product);
        if(success) {
            dao.persist();
            setResponsePage(ProductsPage.class, new PageParameters());
            return true;
        }
        else {
            return false;
        }
    }
    
    
}
