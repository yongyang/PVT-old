package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class ProductNewPage extends TemplatePage {

    Product product = new Product();
    FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");
    Form productForm;
    
    public ProductNewPage(PageParameters pp) {
        this(pp, null);
    }
    
    public ProductNewPage(PageParameters pp, String info) {
    	super(pp, info);
    	
        setActiveMenu(Menu.PRODUCTS);
        add(feedBackPanel);
        
        if (pp != null) {
            String id = pp.get("productId").toString();
        	PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        	product = dao.getPvtModel().getProductbyId(id);
        }
        
        productForm = new Form("form-product", new CompoundPropertyModel(product)) {
            @Override
            protected void onSubmit() {
            	doSubmit();
            }
        };

        TextField<String> nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        nameTextField.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                String inputName = validatable.getValue();
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                boolean existed = false;
                for(Product p : dao.getPvtModel().getProducts()){
                    if(p.getName().equalsIgnoreCase(inputName)) {
                        existed = true;
                        break;
                    }
                }
                if(existed) {
                    ValidationError error = new ValidationError(this);
                    error.setMessage("Product " +  inputName  + " is already existed");
                    validatable.error(error);
                }
            }
        });
        productForm.add(nameTextField);
        productForm.add(new TextField<String>("packages"));
        productForm.add(new TextField<String>("maintainer"));
        productForm.add(new TextField<String>("developer"));
        productForm.add(new TextField<String>("qe"));
        productForm.add(new TextArea<String>("description"));

        add(productForm);
    }
    
    protected void doSubmit(){
    	PageParameters pp = new PageParameters();
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        dao.getPvtModel().addProduct(product);
        dao.persist();
        setResponsePage(new ProductsPage(pp,("Product: " + product.getName() + " Created.")));
    }

}
