package org.jboss.pnc.pvt.wicket;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
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

        List<VerifyTool> tools = getTools(pp);

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
                IModel<String> labelModel = Model.of(item.getModelObject().getName());
                item.add(new Label("tool_label", labelModel));
                item.add(new Label("tool_description", new PropertyModel(item.getModel(), "description")));
                if(tools.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }
            }


        });
    }

    /**
     * Gets Tools according to the page parameters.
     * 
     * @param pp the page parameters, if null, list all tools
     * @return the list of tools specified by the filter in pp.
     */
    private List<VerifyTool> getTools(PageParameters pp) {
        //TODO no filter yet
        PVTDataAccessObject dao = PVTApplication.getDAO();
        return dao.getPvtModel().getToolsList();
    }

}