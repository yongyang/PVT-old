package org.jboss.pnc.pvt.wicket;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
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
public class NewReleasePage extends TemplatePage {

    private Release newRelease = new Release();

    public NewReleasePage() {
        setActiveMenu("releases");

        add(new FeedbackPanel("feedbackMessage"));

        Form newReleaseForm = new Form("form-newrelease", new CompoundPropertyModel(newRelease)) {
            @Override
            protected void onSubmit() {
                System.out.println("Submit: " + newRelease);
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                dao.getPvtModel().addRelease(newRelease);
                dao.persist();

                setResponsePage(new ReleasesPage("Release: " + newRelease.getName() + " Created."));
            }
        };

        newReleaseForm.add(
                new DropDownChoice<Product>(
                        "productName",
                        Model.ofList(((PVTApplication) Application.get()).getDAO().getPvtModel().getProducts()),
                        new ChoiceRenderer<Product>("name", "name")
                ));

        TextField<String> nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        nameTextField.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                String inputName = validatable.getValue();
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                boolean existed = false;
                for(Release p : dao.getPvtModel().getReleases()){
                    if(p.getName().equalsIgnoreCase(inputName)) {
                        existed = true;
                        break;
                    }
                }
                if(existed) {
                    ValidationError error = new ValidationError(this);
                    error.setMessage("Release " + inputName + " is already existed");
                    validatable.error(error);
                }
            }
        });
        newReleaseForm.add(nameTextField);
        newReleaseForm.add(new TextArea<String>("distributions"));
        newReleaseForm.add(new TextArea<String>("description"));

        newReleaseForm.add(new CheckBoxMultipleChoice<String>("jobs", Model.ofList(Arrays.asList("ZipDiff", "Version convention", "JDK version compatible"))));
        add(newReleaseForm);

    }
}
