package org.jboss.pnc.pvt.rest.endpoints;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.Verification;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.wicket.PVTApplication;
import org.jboss.pnc.pvt.wicket.ReleasesPage;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/release", description = "Rest api to issue release verification")
@Path("/release")
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseExecutionEndPoint {

    public ReleaseExecutionEndPoint() {
    }

    @ApiOperation(value = "Start to verify a release with a provided tool name, returns ASAP.")
    @POST
    @Path("/verifyTool/{releaseId}/{toolName}")
    public Response verifyWithToolName(@ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId,
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
        Verification verification = ReleasesPage.runVerify(tool.getId(), release);
        return Response.ok(verification).build();
    }

    @ApiOperation(value = "Start to verify a release, returns ASAP.")
    @POST
    @Path("/verify/{releaseId}")
    public Response verifyRelease(@ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId) {
        if (releaseId == null) {
            return Response.status(Status.BAD_REQUEST).
                    type(MediaType.TEXT_PLAIN_TYPE).build();
        }
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        Release release = pvtModel.getReleasebyId(releaseId);
        if (release == null) {
            return Response.status(Status.NOT_FOUND).
                    type(MediaType.TEXT_PLAIN_TYPE).entity("Release with id: " + releaseId + " is not found.").build();
        }
        ReleasesPage.verifyRelease(release);
        return Response.ok(release.getVerifications().stream().map(p -> pvtModel.getVerificationById(p)).collect(Collectors.toList())).build();
    }

    @ApiOperation(value = "Start to verify a release with provided zip urls, returns ASAP.")
    @POST
    @Path("/verify/{productName}/${releaseName}")
    public Response verifyWithZip(@ApiParam("The product name to be verified") @PathParam("productName") String productName,
            @ApiParam("The product name to be verified") @PathParam("releaseName") String releaseName,
            @ApiParam("The zips of the release") @QueryParam("zips") String zips) {
        if (productName == null || releaseName == null) {
            return Response.status(Status.BAD_REQUEST).
                    type(MediaType.TEXT_PLAIN_TYPE).build();
        }
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        Product prd = pvtModel.getProducts().stream().filter(p -> p.getName().equals(productName)).findAny().orElse(null);
        if (prd == null) {
            return Response.status(Status.NOT_FOUND).
                    type(MediaType.TEXT_PLAIN_TYPE).entity("Product with name: " + productName + " is not found.").build();
        }
        List<Release> releases = pvtModel.getReleasesByProduct(prd.getId());
        Release release = releases.stream().filter(p -> p.getName().equals(releaseName)).findAny().orElse(null);
        if (release == null) {
            release = new Release();
            release.setCreateTime(System.currentTimeMillis());
            release.setDistributions(zips);
            release.setName(releaseName);
            release.setProductId(prd.getId());
//            release.set TODO set tools
            pvtModel.addRelease(release);
        } else {
            release.setDistributions(zips);
        }
        return verifyRelease(release.getId());
    }
}
