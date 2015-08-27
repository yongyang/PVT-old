package org.jboss.pnc.pvt.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wicket.Application;
import org.jboss.pnc.pvt.wicket.PVTApplication;

@Path("/pvtModel")
@Produces(MediaType.APPLICATION_JSON)
public class PVTModelDump {

    @GET
    public Response dump() {
        return Response.ok(((PVTApplication) Application.get()).getDAO().getPvtModel()).build();
    }

}
