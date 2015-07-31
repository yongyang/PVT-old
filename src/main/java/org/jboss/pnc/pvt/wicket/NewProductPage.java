package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class NewProductPage extends TemplatePage {

    private Product newProduct = new Product();

    public NewProductPage() {
        setActiveMenu("products");

        add(new FeedbackPanel("feedbackMessage"));

        Form newProductForm = new Form("form-newproduct", new CompoundPropertyModel(newProduct)) {
            @Override
            protected void onSubmit() {
                System.out.println("Submit: " + newProduct);
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                dao.getPvtModel().addProduct(newProduct);
                dao.persist();

                setResponsePage(new ProductsPage("Product: " + newProduct.getName() + " Created."));
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
        newProductForm.add(nameTextField);
        newProductForm.add(new TextField<String>("packages"));
        newProductForm.add(new TextField<String>("maintainer"));
        newProductForm.add(new TextField<String>("developer"));
        newProductForm.add(new TextField<String>("qe"));
        newProductForm.add(new TextArea<String>("description"));

        add(newProductForm);


    }
}
