package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * Created by yyang on 5/5/15.
 */
@SuppressWarnings("rawtypes")
public class ReleaseNewPage extends TemplatePage {

    private static final long serialVersionUID = -1447200208070180900L;
    protected Release release;
    protected FeedbackPanel feedBackPanel = new FeedbackPanel("feedbackMessage");

    protected Form releaseForm;
    TextField<String> nameTextField;

    protected DropDownChoice<Product> productDropDownChoice;
    protected CheckBoxMultipleChoice<VerifyTool> toolCheckBoxMultipleChoice;
    protected Button resetButton;
    protected Button backButton;
    protected Button removeButton;

    public ReleaseNewPage(PageParameters pp) {
        this(pp,"PVT release to be created.");
    }

    @SuppressWarnings({ "unchecked", "serial" })
    public ReleaseNewPage(PageParameters pp, String info) {
        super(pp,info);
        
        setActiveMenu(Menu.RELEASES);
        release = getRelease(pp);

        add(new FeedbackPanel("feedbackMessage"));
        
        PVTDataAccessObject dao = PVTApplication.getDAO();

        add(new Label("release_summary", getTitle()));


        releaseForm = new Form("form-release", new CompoundPropertyModel(release));

        List<Release> leftRelease = new ArrayList<>(dao.getPvtModel().getReleasesByProduct(release.getProductId()));
        leftRelease.remove(release); // remove current release
        DropDownChoice<Release> previousReleaseIdDropDownChoice = new DropDownChoice<Release>("referenceReleaseId",
                Model.of(),
                leftRelease,
                new IChoiceRenderer<Release>() {
                    @Override
                    public Object getDisplayValue(Release object) {
                        return object.getName();
                    }

                    @Override
                    public String getIdValue(Release object, int index) {
                        return object.getId();
                    }
                }) {

            @Override
            protected void onModelChanged() {
                Release modelR = getModelObject();
                if (modelR != null) {
                    release.setReferenceReleaseId(modelR.getId());
                }
            }
        };
        releaseForm.add(previousReleaseIdDropDownChoice);
        String referenceReleaseId = release.getReferenceReleaseId();
        if (referenceReleaseId != null) {
            previousReleaseIdDropDownChoice.setModelObject(dao.getPvtModel().getReleasebyId(referenceReleaseId));
        }

        Model<Product> listModel = new Model<Product>();
        ChoiceRenderer<Product> productRender = new ChoiceRenderer<Product>("name");
        List<Product> loadProducts = PVTApplication.getDAO().getPvtModel().getProducts();
        productDropDownChoice = new DropDownChoice<Product>("products", listModel, loadProducts , productRender){
        	@Override
            public boolean isNullValid() {
                return true;
            }
        	@Override
    		protected boolean wantOnSelectionChangedNotifications() {
    			return true;
    		}

            @Override
            protected void onSelectionChanged(Product newSelection) {
                super.onSelectionChanged(newSelection);
                String productId = newSelection.getId();
                previousReleaseIdDropDownChoice.setModel(Model.of());
                previousReleaseIdDropDownChoice.setChoices(Model.ofList(dao.getPvtModel().getReleasesByProduct(productId)));
            }
        };
        productDropDownChoice.setRequired(true);

        if (release != null) {
            productDropDownChoice.setModelObject(dao.getPvtModel().getProductById(release.getProductId()));
        }

        releaseForm.add(productDropDownChoice);
        releaseForm.add(new Button("submit") {
            @Override
            public void onSubmit() {
                doSubmit();
            }
        });

        nameTextField = new TextField<String>("name");
        nameTextField.setRequired(true);
        releaseForm.add(nameTextField);
        releaseForm.add(new TextArea<String>("distributions"));
        releaseForm.add(new TextArea<String>("description"));

        toolCheckBoxMultipleChoice = new CheckBoxMultipleChoice<VerifyTool>(
                "tools",
                new ListModel<VerifyTool>(dao.getPvtModel().getVerifyTools(release.getTools())),
                dao.getPvtModel().getToolsList(),
                new IChoiceRenderer<VerifyTool>() {
                    @Override
                    public Object getDisplayValue(VerifyTool object) {
                        return object;
                    }

                    @Override
                    public String getIdValue(VerifyTool object, int index) {
                        return object == null ? null : object.getId();
                    }
                });
        releaseForm.add(toolCheckBoxMultipleChoice);

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

        removeButton = new AjaxButton("remove") {

            boolean success = false;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                success = doRemove();
            }


            @Override
            protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onAfterSubmit(target, form);
                if(!success) {
                    target.appendJavaScript("alert('Remove Release failed, usually because this release has link data!')");
                }
                else {
                    PageParameters pp = new PageParameters();
                    setResponsePage(new ReleasesPage(pp, "Release: " + release.getName() + " is removed."));
                }
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
                PVTDataAccessObject dao = PVTApplication.getDAO();
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


    protected Release getRelease(PageParameters pp){
        return new Release();
    }

    public String getTitle() {
        return "Create a Release";
    }

    public void doSubmit() {

        PVTDataAccessObject dao = PVTApplication.getDAO();

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

    public boolean doRemove() {
        return false;
    }

}
