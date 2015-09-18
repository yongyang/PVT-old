package org.jboss.pnc.pvt.wicket;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.JDKCompatibleVerifyTool;
import org.jboss.pnc.pvt.model.JDKCompatibleVerifyTool.JDK;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class JDKCompatibleVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public JDKCompatibleVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public JDKCompatibleVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);

        JDKCompatibleVerifyTool jdkVerifyTool = (JDKCompatibleVerifyTool) tool;

        final IChoiceRenderer<JDK> JDKDropChoiseRender = new IChoiceRenderer<JDK>() {
            @Override
            public Object getDisplayValue(JDK jdk) {
                return jdk.name();
            }

            @Override
            public String getIdValue(JDK jdk, int index) {
                return jdk.name();
            }

        };
        DropDownChoice<JDK> maxJDKDropDownChoice = new DropDownChoice<JDK>("maxJDK", Model.of(jdkVerifyTool.getMaxJDK()),
                Arrays.asList(JDK.values()), JDKDropChoiseRender) {

            @Override
            public boolean isNullValid() {
                return false;
            }

            @Override
            protected void onModelChanged() {
                jdkVerifyTool.setMaxJDK(getModelObject());
            }
        };
        maxJDKDropDownChoice.setRequired(true);

        DropDownChoice<JDK> minJDKDropDownChoice = new DropDownChoice<JDK>("minJDK", Model.of(jdkVerifyTool.getMinJDK()),
                Arrays.asList(JDK.values()), JDKDropChoiseRender) {

            @Override
            public boolean isNullValid() {
                return false;
            }

            @Override
            protected void onModelChanged() {
                jdkVerifyTool.setMinJDK(getModelObject());
            }
        };
        minJDKDropDownChoice.setRequired(true);
        minJDKDropDownChoice.add(new IValidator<JDK>() {

            @Override
            public void validate(IValidatable<JDK> validatable) {
                JDK jdk = validatable.getValue();
                if (jdk.compareTo(JDK.valueOf(maxJDKDropDownChoice.getValue())) > 0) {
                    ValidationError error = new ValidationError();
                    error.addKey("minjdk.maxjdk").setVariable("minJDK", jdk.name())
                            .setVariable("maxJDK", maxJDKDropDownChoice.getValue());
                    validatable.error(error);
                }
            }
        });

        form.add(minJDKDropDownChoice);
        form.add(maxJDKDropDownChoice);
        form.add(new CheckBox("fastReturn", new PropertyModel<Boolean>(jdkVerifyTool, "fastReturn")));

    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new JDKCompatibleVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a new JDK compatible verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new JDKCompatibleVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}
