package org.jboss.pnc.pvt.rest.endpoints;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.Verification;
import org.jboss.pnc.pvt.model.VerifyParameter;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.wicket.PVTApplication;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/release", description = "Rest api to issue release verification")
@Path("/release")
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseExecutionEndPoint {

    public ReleaseExecutionEndPoint() {
    }

    @ApiOperation(value = "Start to verify a release with a buildId provided, returns ASAP.")
    @POST
    @Path("/verify/{releaseId}/{toolName}")
    public Response verifyWithBuildId(@ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId,
            @ApiParam("The tool name used to verify the release") @PathParam("toolName") String toolName) {
        if (releaseId == null || toolName == null) {
            return Response.status(Status.BAD_REQUEST).
                    type(MediaType.TEXT_PLAIN_TYPE).build();
        }
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        Release release = pvtModel.getReleasebyId(releaseId);
        if (release == null) {
            return Response.status(Status.NOT_FOUND).
                    type(MediaType.TEXT_PLAIN_TYPE).entity("Release with id: " + releaseId + " is not found.").build();
        }
        VerifyTool tool = pvtModel.getToolsList().stream().filter(p -> p.getName().equals(toolName)).findAny().orElse(null);
        if (tool == null) {
            return Response.status(Status.NOT_FOUND).
                    type(MediaType.TEXT_PLAIN_TYPE).entity("Tool with name: " + toolName + " is not found.").build();
        }
        if (release.getTools().contains(tool.getId()) == false) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).
                    type(MediaType.TEXT_PLAIN_TYPE).entity("Tool: " + toolName + " is not associated with release: " + release.getName()).build();
        }
        VerifyParameter parameter = new VerifyParameter(tool.getId(), release);
        Verification verification = tool.verify(parameter);
        release.addVerification(verification.getToolId(), verification.getId());
        release.setStatus(Release.Status.VERIFYING);
        pvtModel.addVerification(verification);
        pvtModel.updateRelease(release);
        return Response.ok(verification).build();
    }

    @ApiOperation(value = "Start to verify a release with a buildId provided, returns ASAP.")
    @POST
    @Path("/verify/{releaseId}/{zips}")
    public Response verifyWithZip(@ApiParam("The release id want to verify") @PathParam("releaseId") String productName,
            @ApiParam("The zips of the release") @PathParam("zips") String version) {
        return Response.ok().build();
    }
}
