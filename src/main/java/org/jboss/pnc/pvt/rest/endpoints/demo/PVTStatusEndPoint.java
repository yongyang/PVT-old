package org.jboss.pnc.pvt.rest.endpoints.demo;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.pnc.pvt.rest.model.VerificationMeta;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/pvtStatus")
public class PVTStatusEndPoint {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response onStatus(List<VerificationMeta> metas) throws IOException {
        PVTStatusReceiver.StatusReveivorRegistry.INSTANCE.broadMessage(new ObjectMapper().writeValueAsString(metas));
        return Response.status(200).entity("Status sent to websocket client").build();
    }

}
