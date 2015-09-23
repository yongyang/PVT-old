package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.model.VerifyToolType;

/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({"serial", "rawtypes"})
public class ToolsPage extends TemplatePage {

    public ToolsPage(PageParameters pp) {
        this(pp, "PVT verify tools loaded.");
    }

    public ToolsPage(PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu(Menu.TOOLS);

        List<VerifyToolType> toolTypes = PVTApplication.getDAO().getPvtModel().getToolTypes();
        StringValue strValue = pp.get("filter");
        String filter = null;
        if (strValue != null) {
            filter = strValue.toOptionalString();
        }
        final TextField<String> filterText = new TextField<String>("filter", filter == null ? Model.of("") : Model.of(filter));
        Form<String> searchForm = new Form<String>("tools_form") {

            @Override
            protected void onSubmit() {
                String filter = getRequest().getPostParameters().getParameterValue("filter").toOptionalString();
                PageParameters pp = new PageParameters();
                pp.set("filter", filter);
                setResponsePage(ToolsPage.class, pp);
            }
        };
        searchForm.add(filterText);
        searchForm.setMarkupId("tools_form");
        add(searchForm);

        add(new ListView<VerifyToolType>("tool_lables", toolTypes) {
            @Override
            protected void populateItem(final ListItem<VerifyToolType> item) {
                item.add(new Link<String>("link-tool") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.add("label", item.getModelObject());

                        String implClass = item.getModel().getObject().getImplClass();
                        String newPageClassName = ToolsPage.class.getPackage().getName() + implClass.substring(implClass.lastIndexOf(".")) + "NewPage";
                        try {
                            Class newPageClass = getClass().getClassLoader().loadClass(newPageClassName);
                            setResponsePage(newPageClass, pp);
                        }
                        catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public IModel<?> getBody() {
                        return Model.of(item.getModelObject().getName());
                    }
                });
            }
        });

        List<VerifyTool> tools = getTools(filter);

        add(new Label("tool_count", Model.of("" + tools.size())));

        add(new ListView<VerifyTool>("tool_rows", tools) {

            @Override
            protected void populateItem(ListItem<VerifyTool> item) {
                item.add(new Link<String>("tool_link") {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onClick() {
                    	PageParameters pp = new PageParameters();
                        pp.add("id", item.getModel().getObject().getId());
                        String toolClsName = item.getModel().getObject().getClass().getSimpleName();
                        String editPageClsName = ToolsPage.class.getPackage().getName() + "." + toolClsName + "EditPage";
                        try {
                            Class editPageCls = getClass().getClassLoader().loadClass(editPageClsName);
                            setResponsePage(editPageCls, pp);
                        }
                        catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "name");
                    }
                });
                PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
                String toolType = pvtModel.getToolTypes().stream()
                        .filter(t -> item.getModelObject().getClass().getName().equals(t.getImplClass()))
                        .findAny()
                        .get().getName();
                item.add(new Label("tool_type", Model.of(toolType)));
                item.add(new Label("tool_description", new PropertyModel(item.getModel(), "description")));
                if(tools.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }
            }


        });
    }

    /**
     * Gets Tools according to the filter.
     * 
     * @param pp the page parameters, if null, list all tools
     * @return the list of tools specified by the filter in pp.
     */
    private List<VerifyTool> getTools(String filter) {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        List<VerifyTool> tools = dao.getPvtModel().getToolsList();
        List<VerifyTool> result = new ArrayList<>();
        for (VerifyTool tool: tools) {
            if (filter != null && filter.trim().length() > 0) {
                if (tool.getName().toLowerCase().contains(filter.toLowerCase())) {
                    result.add(tool);
                }
            } else {
                result.add(tool);
            }
        }
        return result;
    }

}