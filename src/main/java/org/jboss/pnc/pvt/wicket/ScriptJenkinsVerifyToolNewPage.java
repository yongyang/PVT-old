package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.execution.ExecutionVariable;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.VerifyTool;

/**
 * A web-page used to edit/create a Verification Tool.
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({ "serial" })
public class ScriptJenkinsVerifyToolNewPage extends AbstractVerifyToolPage {

    private static final long serialVersionUID = 1L;

    public ScriptJenkinsVerifyToolNewPage(PageParameters pp) {
        this(pp, null);
    }

    public ScriptJenkinsVerifyToolNewPage(PageParameters pp, String info) {
        super(pp, info);
        form.add(new TextArea<String>("script"));
        form.add(new TextField<String>("jobId"));
        form.add(new TextField<String>("archiver"));

        List<ExecutionVariable> varables = new ArrayList<>();
        varables.addAll(ExecutionVariable.getVariables().values());
        Collections.sort(varables, new Comparator<ExecutionVariable>() {

            @Override
            public int compare(ExecutionVariable o1, ExecutionVariable o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        form.add(new Link<String>("varLink") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                String ctxPath = PVTApplication.get().getServletContext().getContextPath();
                tag.put("onclick", "showVariables('" + ctxPath + "');");
            }

            @Override
            public void onClick() {
            }
        });
    }

    @Override
    protected VerifyTool getVerifyTool(PageParameters pp) {
        return new ScriptJenkinsVerifyTool();
    }

    @Override
    protected String getTitle() {
        return "Define a script jenkins verify tool";
    }

    @Override
    protected void doSubmit(PageParameters pp) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        dao.getPvtModel().addTool(tool);
        dao.persist();
        pp.add("id", tool.getId());
        setResponsePage(new ScriptJenkinsVerifyToolEditPage(pp, "Tool: " + form.getModelObject().getName() + " is created."));
    }
}