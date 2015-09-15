package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
import org.jboss.pnc.pvt.model.Release;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class ProductNewPage extends TemplatePage {

	protected Product product;
	protected FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");
	protected Form productForm;
	
	TextField<String> nameTextField;
	protected Button resetButton;
    protected Button backButton;
    protected Button removeButton;
    
    public ProductNewPage(PageParameters pp) {
        this(pp, "PVT prodcut to be created.");
    }
    
    public ProductNewPage(PageParameters pp, String info) {
    	super(pp, info);
    	
        setActiveMenu(Menu.PRODUCTS);
        product = getProduct(pp);
        add(feedBackPanel);
        add(new Label("product_summary", getTitle()));
        
        productForm = new Form("form-product", new CompoundPropertyModel(product));

        nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        productForm.add(nameTextField);
        productForm.add(new TextField<String>("packages"));
        productForm.add(new TextField<String>("maintainer"));
        productForm.add(new TextField<String>("developer"));
        productForm.add(new TextField<String>("qe"));
        productForm.add(new TextArea<String>("description"));
        
        productForm.add(new Button("submit") {
            @Override
            public void onSubmit() {
                doSubmit();
            }
        });
        
        backButton = new Button("back") {
            @Override
            public void onSubmit() {
                PageParameters pp = new PageParameters();
                setResponsePage(ProductsPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);
        productForm.add(backButton);
        
        resetButton = new Button("reset") {
            @Override
            public void onSubmit() {
                doReset();
            }
        };
        productForm.add(resetButton);
        
        removeButton = new Button("remove") {
            @Override
            public void onSubmit() {
                doRemove();
            }
        };
        removeButton.setDefaultFormProcessing(false);
        productForm.add(removeButton);

        add(productForm);
    }
    
    protected Product getProduct(PageParameters pp){
        return new Product();
    }
    
    public String getTitle() {
        return "Create a Product";
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        removeButton.setVisible(false);
    }
    
    public void doSubmit(){
    	validator();
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addProduct(product);
        dao.persist();
        PageParameters pp = new PageParameters();
    	pp.set("name", product.getName());
        setResponsePage(new ProductsPage(pp,("Product: " + product.getName() + " Created.")));
    }
    
    public void doReset() {
    	productForm.getModel().setObject(new Release());
    }

    public boolean doRemove() {
        return false;
    }
    
    public void validator(){
    	nameTextField.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                String inputName = validatable.getValue();
                PVTDataAccessObject dao = PVTApplication.getDAO();
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
    }

}
