package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;

public class EditProductPage extends TemplatePage {
	
    private Product editProduct = new Product();

    public EditProductPage() {
    	setActiveMenu("products");
    	editProduct = (Product) Session.get().getAttribute("product");
    	System.out.println("product name ======="+editProduct.getName());

        //add(new FeedbackPanel("feedbackMessage"));

        Form editProductForm = new Form("form-editproduct", new CompoundPropertyModel(editProduct)) {
            @Override
            protected void onSubmit() {
                System.out.println("Submit: " + editProduct);
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                dao.getPvtModel().updateProduct(editProduct);
                dao.persist();

                setResponsePage(new ProductsPage("Product: " + editProduct.getName() + " Updated."));
            }
        };

        
        editProductForm.add(new TextField<String>("name"));
        editProductForm.add(new TextField<String>("packages"));
        editProductForm.add(new TextField<String>("maintainer"));
        editProductForm.add(new TextField<String>("developer"));
        editProductForm.add(new TextField<String>("qe"));
        editProductForm.add(new TextArea<String>("description"));

        add(editProductForm);


    }
	
}
