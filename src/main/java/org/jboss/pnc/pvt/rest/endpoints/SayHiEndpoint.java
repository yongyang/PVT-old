package org.jboss.pnc.pvt.rest.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/sayhi", description = "Say Hi")
@Path("/sayhi")
@Produces(MediaType.APPLICATION_JSON)
public class SayHiEndpoint {


    public SayHiEndpoint() {
    }

    @ApiOperation(value = "Say Hi to UI")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHi() {
        return Response.ok("Hi, Swagger!").build();
    }

    @ApiOperation(value = "Say Hi to UI")
    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHello(@ApiParam("Name to say hi") @PathParam("name") String name) {
        return Response.ok("Hi " + name + "!").build();
    }
}
