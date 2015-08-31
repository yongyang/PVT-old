package org.jboss.pnc.pvt.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.pnc.pvt.wicket.PVTApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/pvtModel")
@Produces(MediaType.APPLICATION_JSON)
public class PVTModelDump {

    @GET
    public Response dump() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(PVTApplication.getDAO().getPvtModel());
            return Response.ok(str).build();
        } catch (JsonProcessingException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(e.getMessage()).build();
        }
    }
}