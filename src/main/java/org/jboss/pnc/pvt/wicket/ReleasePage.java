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
public class ReleasePage extends TemplatePage {
	
	private static int MODE_EDIT = 1;

    // view mode is not used for now.
    private static int MODE_VIEW = 2;

    private static int MODE_CREATE = 3;
    /**
     * page mode, default to MODE_EDIT
     */
    private int mode = MODE_EDIT;
	
	private Release release = new Release();
    private FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");
    private Form releaseForm;
    
    public ReleasePage(PageParameters pp) {
        this(pp,"PVT release to be created.");
    }

    public ReleasePage(PageParameters pp, String info) {
        super(pp,info);
        
        setActiveMenu("releases");
        add(new FeedbackPanel("feedbackMessage"));
        
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();

        if (pp != null) {
        	if (!pp.get("releaseId").isNull())
            	release = dao.getPvtModel().getReleasebyId(pp.get("releaseId").toString());
        	if (!pp.get("mode").isNull())
        		mode = pp.get("mode").toInt();
        }
        if (mode == MODE_EDIT || mode == MODE_VIEW) {
            add(new Label("release_summary", "Edit the Release"));
        } else if (mode == MODE_CREATE) {
            add(new Label("release_summary", "Create a Release"));
        } else {
            throw new IllegalArgumentException("Wrong mode parameter: mode = " + mode);
        }
        
        Model<Product> listModel = new Model<Product>();
        ChoiceRenderer<Product> productRender = new ChoiceRenderer<Product>("name");
        List<Product> loadProducts = ((PVTApplication) Application.get()).getDAO().getPvtModel().getProducts();
        DropDownChoice<Product> productDropDownChoice = new DropDownChoice<Product>("products", listModel, loadProducts , productRender){
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
        		
        		release.setProductId(productDropDownChoice.getModelObject().getId());
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                	dao.getPvtModel().updateRelease(release);
                    dao.persist(); 
                    setInfo("Release: " + release.getName() + " is Updated.");
                } else if (mode == MODE_CREATE) {
                	dao.getPvtModel().addRelease(release);
                    dao.persist();
                    PageParameters pp = new PageParameters();
                    pp.set("mode", MODE_EDIT + "");
                    pp.set("name", release.getName());
                    setResponsePage(new ReleasesPage(pp, "Release: " + release.getName() + " is created."));
                } else {
                    throw new IllegalArgumentException("Wrong mode parameter: mode = " + mode);
                }
            }
        	
        };

        releaseForm.add(productDropDownChoice);
        TextField<String> nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        releaseForm.add(nameTextField);
        releaseForm.add(new TextArea<String>("distributions"));
        releaseForm.add(new TextArea<String>("description"));
        releaseForm.add(new CheckBoxMultipleChoice<String>("jobs", Model.ofList(Arrays.asList("ZipDiff", "Version convention", "JDK version compatible"))));

        releaseForm.add(new IFormValidator(){
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
        
        
        Button backButton = new Button("back") {

            @Override
            public void onSubmit() {
                PageParameters pp = new PageParameters();
                setResponsePage(ReleasesPage.class, pp);
            }
        };
        backButton.setDefaultFormProcessing(false);
        releaseForm.add(backButton);

        Button resetButton = new Button("reset") {
            @Override
            public void onSubmit() {
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                	releaseForm.getModel().setObject(release);
                } else {
                	releaseForm.getModel().setObject(new Release());
                }
            }
        };
        releaseForm.add(resetButton);

        Button removeButton = new Button("remove") {

            @Override
            public void onSubmit() {
                dao.getPvtModel().removeRelease(release);
                dao.persist();
                PageParameters pp = new PageParameters();
                setResponsePage(new ReleasesPage(pp, "Tool: " + release.getName() + " is removed."));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(true);
                setVisible(mode == MODE_EDIT || mode == MODE_VIEW);
            }

            @Override
            public boolean isVisible() {
                if (mode == MODE_EDIT || mode == MODE_VIEW) {
                    return true;
                }
                return false;
            }
        };
        releaseForm.add(removeButton);
        removeButton.setVisible(mode == MODE_EDIT || mode == MODE_VIEW);

        add(releaseForm);
    }
}
