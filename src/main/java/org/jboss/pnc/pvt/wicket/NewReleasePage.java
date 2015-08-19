package org.jboss.pnc.pvt.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;

import com.googlecode.wicket.kendo.ui.form.TextArea;
import com.googlecode.wicket.kendo.ui.form.TextField;

/**
 * Created by yyang on 5/5/15.
 */
public class NewReleasePage extends TemplatePage {
	

	protected Release release = new Release();
    protected FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");
    protected Form releaseForm;
    TextField<String> nameTextField;

    protected DropDownChoice<Product> productDropDownChoice;
    protected Button resetButton;
    protected Button backButton;
    protected Button removeButton;

    public NewReleasePage(PageParameters pp) {
        this(pp,"PVT release to be created.");
    }

    public NewReleasePage(PageParameters pp, String info) {
        super(pp,info);
        
        setActiveMenu("releases");
        add(new FeedbackPanel("feedbackMessage"));
        
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();

        if (pp != null) {
        	if (!pp.get("releaseId").isNull())
            	release = dao.getPvtModel().getReleasebyId(pp.get("releaseId").toString());
        }
        add(new Label("release_summary", getTitle()));

        Model<Product> listModel = new Model<Product>();
        ChoiceRenderer<Product> productRender = new ChoiceRenderer<Product>("name");
        List<Product> loadProducts = ((PVTApplication) Application.get()).getDAO().getPvtModel().getProducts();
        productDropDownChoice = new DropDownChoice<Product>("products", listModel, loadProducts , productRender){
        	@Override
            public boolean isNullValid() {
                return true;
            }
        	@Override
    		protected boolean wantOnSelectionChangedNotifications() {
    			return true;
    		}
        };
        productDropDownChoice.setRequired(true);
        if (release != null)
        	productDropDownChoice.setModelObject(dao.getPvtModel().getProductbyId(release.getProductId()));
        
        
        releaseForm = new Form("form-release", new CompoundPropertyModel(release)) {
        	@Override
            protected void onSubmit() {
                doSubmit();
            }
        	
        };

        releaseForm.add(productDropDownChoice);
        nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        releaseForm.add(nameTextField);
        releaseForm.add(new TextArea<String>("distributions"));
        releaseForm.add(new TextArea<String>("description"));
        releaseForm.add(new CheckBoxMultipleChoice<String>("jobs", Model.ofList(Arrays.asList("ZipDiff", "Version convention", "JDK version compatible"))));

        backButton = new Button("back") {

            @Override
            public void onSubmit() {
                PageParameters pp = new PageParameters();
                setResponsePage(ReleasesPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);
        releaseForm.add(backButton);

        resetButton = new Button("reset") {
            @Override
            public void onSubmit() {
                doReset();
            }
        };
        releaseForm.add(resetButton);

        removeButton = new Button("remove") {

            @Override
            public void onSubmit() {
                doRemove();
            }
        };
        releaseForm.add(removeButton);

        add(releaseForm);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        removeButton.setVisible(false);

        releaseForm.add(new IFormValidator() {
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{nameTextField, productDropDownChoice};
            }

            public void validate(Form<?> form) {
                PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
                boolean existed = false;
                for (Release rel : dao.getPvtModel().getReleases()) {
                    if ((!rel.getId().equalsIgnoreCase(release.getId())) && rel.getName().equalsIgnoreCase(nameTextField.getInput()) && rel.getProductId().equalsIgnoreCase(productDropDownChoice.getInput())) {
                        existed = true;
                        break;
                    }
                }
                if (existed) {
                    ValidationError error = new ValidationError();
                    error.setMessage("The Release " + productDropDownChoice.getInput() + "-" + nameTextField.getInput() + " is already existed");
                    nameTextField.error(error);
                }
            }
        });
    }




    public String getTitle() {
        return "Create a Release";
    }

    public void doSubmit() {

        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();

        release.setProductId(productDropDownChoice.getModelObject().getId());
        dao.getPvtModel().addRelease(release);
        dao.persist();
        PageParameters pp = new PageParameters();
        pp.set("name", release.getName());
        setResponsePage(new ReleasesPage(pp, "Release: " + release.getName() + " is created."));
    }

    public void doReset() {
        releaseForm.getModel().setObject(new Release());
    }

    public void doRemove() {

    }

}
