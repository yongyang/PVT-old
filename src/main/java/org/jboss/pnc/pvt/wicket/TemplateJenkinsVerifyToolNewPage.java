package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.dom4j.DocumentException;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob.SerializableStringParam;
import org.jboss.pnc.pvt.model.TemplateJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class TemplateJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    private final ListView<SerializableStringParam> paramsListView;
    private final Label archiverLabel;
    private final TextArea<String> configXMLTxtArea;

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public TemplateJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        configXMLTxtArea = new TextArea<String>("jenkinsConfigXML");
        configXMLTxtArea.setLabel(Model.of("Jenkins Config XML"));
        configXMLTxtArea.add(new IValidator<String>() {

            @Override
            public void validate(IValidatable<String> validatable) {
                String xml = validatable.getValue();
                ValidationError error = new ValidationError();
                try {
                    new ParamJenkinsJob(xml);
                } catch (DocumentException e) {
                    error.addKey("xmlInvalid");
                    validatable.error(error);
                    return;
                }
            }
        });
        form.add(configXMLTxtArea);
        form.add(new TextField<String>("jobId"));

        final MarkupContainer stringParamsPanel = new WebMarkupContainer("stringParamsPanel");
        stringParamsPanel.setOutputMarkupId(true);

        paramsListView = new ListView<SerializableStringParam>("stringParams", new ArrayList<>()) {

            @Override
            protected void populateItem(final ListItem<SerializableStringParam> item) {
                item.add(new Label("name", item.getModelObject().getName()));
                item.add(new Label("description", item.getModelObject().getDescription()));
                item.add(new Label("defaultValue", item.getModelObject().getDefaultValue()));
            }
        };
        stringParamsPanel.add(paramsListView);
        form.add(stringParamsPanel);

        archiverLabel = new Label("archiver", "");
        archiverLabel.setOutputMarkupId(true);
        form.add(archiverLabel);

        IndicatingAjaxLink<String> checkJobLink = new IndicatingAjaxLink<String>("checkJob") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                configXMLTxtArea.validate();
                if (configXMLTxtArea.isValid()) {
                    try {
                        ParamJenkinsJob paramJob = new ParamJenkinsJob(Strings.unescapeMarkup(configXMLTxtArea.getValue()).toString());
                        List<SerializableStringParam> stringParams = paramJob.getStringParams();
                        paramsListView.setModelObject(stringParams);

                        // check job archiver
                        String archiver = paramJob.getArchiver();
                        if (archiver != null) {
                            archiverLabel.setDefaultModelObject(archiver);
                        } else {
                            archiverLabel.setDefaultModelObject("No Archive specified!");
                        }
                    } catch (DocumentException e) {
                        setInfo(e.getMessage());//TODO how to display the message ?
                    }
                    if (target != null) {
                        target.add(stringParamsPanel);
                        target.add(archiverLabel);
                    }
                }
            }
        };
        form.add(checkJobLink);
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new TemplateJenkinsVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a template jenkins verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new TemplateJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}
