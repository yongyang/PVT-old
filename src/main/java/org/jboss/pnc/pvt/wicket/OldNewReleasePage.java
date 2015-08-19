package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class OldNewReleasePage extends TemplatePage {

    private Release newRelease = new Release();
    
    public OldNewReleasePage(PageParameters pp) {
        this(pp,"PVT release to be created.");
    }

    public OldNewReleasePage(PageParameters pp, String info) {
    	super(pp,info);
    	
        setActiveMenu("releases");
        add(new FeedbackPanel("feedbackMessage"));

        Form newReleaseForm = new Form("form-newrelease", new CompoundPropertyModel(newRelease)) {
            @Override
            protected void onSubmit() {
                System.out.println("Submit: " + newRelease);
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                dao.getPvtModel().addRelease(newRelease);
                dao.persist();

                setResponsePage(new ReleasesPage(pp,"Release: " + newRelease.getName() + " Created."));
            }
        };


        List<String> productNames = new ArrayList<>();
        for(Product p : ((PVTApplication) Application.get()).getDAO().getPvtModel().getProducts()) {
            productNames.add(p.getName());
        }

        DropDownChoice<String> productDropDownChoice = new DropDownChoice<String>("productName", Model.ofList(productNames), new ChoiceRenderer<String>("toString", "toString") {
        }) {
            @Override
            public boolean isNullValid() {
                return true;
            }

        };
        productDropDownChoice.setRequired(true);
        newReleaseForm.add(productDropDownChoice);

        TextField<String> nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);

        newReleaseForm.add(nameTextField);
        newReleaseForm.add(new TextArea<String>("distributions"));
        newReleaseForm.add(new TextArea<String>("description"));
        newReleaseForm.add(new CheckBoxMultipleChoice<String>("jobs", Model.ofList(Arrays.asList("ZipDiff", "Version convention", "JDK version compatible"))));

        newReleaseForm.add(new IFormValidator(){
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{nameTextField, productDropDownChoice};
            }

            public void validate(Form<?> form) {
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                boolean existed = false;
                for(Release rel : dao.getPvtModel().getReleases()){
                    if(rel.getName().equalsIgnoreCase(nameTextField.getInput()) && rel.getProductId().equalsIgnoreCase(productDropDownChoice.getInput())) {
                        existed = true;
                        break;
                    }
                }
                if(existed) {
                    ValidationError error = new ValidationError();
                    error.setMessage("The Release " + productDropDownChoice.getInput() + "-" + nameTextField.getInput()  + " is already existed");
                    nameTextField.error(error);
                }
            }
        });

        add(newReleaseForm);

    }


}
