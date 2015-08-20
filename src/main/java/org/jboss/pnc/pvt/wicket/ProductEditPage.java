package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;

/**
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class ProductEditPage extends TemplatePage {
	
	Product product = new Product();
    FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");
    Form productForm;
    
    public ProductEditPage(PageParameters pp) {
        this(pp, null);
    }
    
    public ProductEditPage(PageParameters pp, String info) {
    	super(pp, info);
    	
        setActiveMenu("products");
        add(feedBackPanel);
        
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        if (pp != null) {
        	if (!pp.get("productId").isNull())
        		product = dao.getPvtModel().getProductbyId(pp.get("productId").toString());
        }
        
        productForm = new Form("form-product", new CompoundPropertyModel(product)) {
            @Override
            protected void onSubmit() {
            	PageParameters pp = new PageParameters();
                dao.getPvtModel().updateProduct(product);
                dao.persist();                        
                setResponsePage(new ProductsPage(pp,("Product: " + product.getName() + " Updated.")));
            }
        };
        
        productForm.add(new TextField<String>("name"));
        productForm.add(new TextField<String>("packages"));
        productForm.add(new TextField<String>("maintainer"));
        productForm.add(new TextField<String>("developer"));
        productForm.add(new TextField<String>("qe"));
        productForm.add(new TextArea<String>("description"));
        
        Button backButton = new Button("back"){
        	@Override
			public void onSubmit() {
        		PageParameters pp = new PageParameters();
                setResponsePage(ProductsPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);
        
        Button removeButton = new Button("remove"){
        	@Override
			public void onSubmit() {  
        		PageParameters pp = new PageParameters();
        		PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                dao.getPvtModel().removeProduct(product);
                dao.persist();
                setResponsePage(ProductsPage.class, pp);
            }
        };
        removeButton.setDefaultFormProcessing(false);
        
        productForm.add(backButton);
        productForm.add(removeButton);
        
        add(productForm);
        
    }
	
}
