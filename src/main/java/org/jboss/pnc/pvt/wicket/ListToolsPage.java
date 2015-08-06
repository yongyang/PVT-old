package org.jboss.pnc.pvt.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool.Level;
import org.jboss.pnc.pvt.model.ScriptJenkinsVerifyTool.Type;


/**
 * 
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@SuppressWarnings({"serial", "rawtypes"})
public class ListToolsPage extends TemplatePage {

    public ListToolsPage(PageParameters pp) {
        this(pp, "PVT tools loaded.");
    }

    private static List<ScriptJenkinsVerifyTool> tools = new ArrayList<ScriptJenkinsVerifyTool>();
    static {
        ScriptJenkinsVerifyTool tool = new ScriptJenkinsVerifyTool();
        tool.setScript("java -jar tool.jar Main ${args1} ${args2}");
        tool.setId(1L);
        tool.setName("My Tool1");
        tool.setDescription("Test tool to use 2 variables");
        tool.setLevel(Level.ALL);
        tool.setType(Type.STATIC);
        
        tools.add(tool);

        tool = new ScriptJenkinsVerifyTool();
        tool.setScript("java -jar tool.jar SecMain ${args1} ${args2}");
        tool.setId(2L);
        tool.setName("My Tool2");
        tool.setDescription("Test tool to use 2 variables of SecMain");
        tool.setLevel(Level.PRODUCT);
        tool.setType(Type.RUNTIME);
        
        tools.add(tool);
    }

    static List<ScriptJenkinsVerifyTool> getAllTools() {
        return tools;
    }

    public ListToolsPage(PageParameters pp, String info) {
        super(pp, info);
        setActiveMenu("tools");

        add(new Link<String>("link-tool") {
            @Override
            public void onClick() {
                PageParameters pp = new PageParameters();
                pp.set("mode", "3"); // MODE_CREATE
                setResponsePage(SingleToolPage.class, pp);
            }
        });

        List<ScriptJenkinsVerifyTool> tools = getAllTools();

        add(new Label("tool_count", Model.of("" + tools.size())));

        add(new ListView<ScriptJenkinsVerifyTool>("tool_rows", tools) {
            @Override
            protected void populateItem(ListItem<ScriptJenkinsVerifyTool> item) {
                item.add(new Link<String>("tool_link") {
                    @Override
                    public void onClick() {
                    	PageParameters pp = new PageParameters();
                        pp.add("id", item.getModel().getObject().getId());
                        setResponsePage(SingleToolPage.class, pp);
                    }

                    @Override
                    public IModel<?> getBody() {
                        return new PropertyModel(item.getModel(), "name");
                    }
                });
                item.add(new Label("tool_type", new PropertyModel(item.getModel(), "type")));
                item.add(new Label("tool_level", new PropertyModel(item.getModel(), "level")));
                item.add(new Label("tool_description", new PropertyModel(item.getModel(), "description")));
                if(tools.indexOf(item.getModel().getObject()) % 2 == 1) {
                    item.add(AttributeModifier.replace("class", "errata_row odd"));
                }
            }
        });
    }
}