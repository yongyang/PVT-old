package org.jboss.pnc.pvt.rest.endpoints;

import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.rest.model.ToolMeta;
import org.jboss.pnc.pvt.wicket.PVTApplication;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "/tools", description = "List available tool names")
@Path("/tools")
@Produces(MediaType.APPLICATION_JSON)
public class ToolsEndPoint {

    @ApiOperation(value = "List all available tool names")
    @GET
    public Response toolsList() {
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        return Response.ok(pvtModel.getToolsList().stream().map(t -> new ToolMeta().setId(t.getId()).setName(t.getName())).collect(Collectors.toList())).build();
    }

}
