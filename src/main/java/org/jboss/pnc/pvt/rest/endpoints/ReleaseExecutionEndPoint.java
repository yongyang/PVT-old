package org.jboss.pnc.pvt.rest.endpoints;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.jboss.pnc.pvt.execution.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Api(value = "/release", description = "Rest api to issue release verification")
@Path("/release")
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseExecutionEndPoint {


    public ReleaseExecutionEndPoint() {
    }

    @ApiOperation(value = "Start to verify a release with a buildId provided, returns ASAP.")
    @POST
    @Path("/verify/{releaseId}/{buildId}")
    public Response verifyWithBuildId(
            @ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId,
            @ApiParam("The buildId of the release") @PathParam("buildId") String buildId
            ) {
        return Response.ok().build();
    }

    @ApiOperation(value = "Start to verify a release with a buildId provided, returns ASAP.")
    @POST
    @Path("/verify/{releaseId}/{zips}")
    public Response verifyWithZip(
            @ApiParam("The release id want to verify") @PathParam("releaseId") String productName,
            @ApiParam("The zips of the release") @PathParam("zips") String version
    ) {
        return Response.ok().build();
    }
}
