package org.jboss.pnc.pvt.rest.endpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;
import org.jboss.pnc.pvt.model.Release;
import org.jboss.pnc.pvt.model.Verification;
import org.jboss.pnc.pvt.model.VerifyTool;
import org.jboss.pnc.pvt.rest.model.VerificationMeta;
import org.jboss.pnc.pvt.wicket.PVTApplication;
import org.jboss.pnc.pvt.wicket.ReleasesPage;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/release", description = "Rest api to issue release verification")
@Path("/release")
@Produces(MediaType.APPLICATION_JSON)
public class ReleaseExecutionEndPoint {

    private static final Logger logger = Logger.getLogger(ReleaseExecutionEndPoint.class);

    public ReleaseExecutionEndPoint() {
    }

    @ApiOperation(value = "Start to verify a release with a provided tool name, returns ASAP.")
    @POST
    @Path("/verifyTool/{releaseId}/{toolName}")
    public Response verifyWithToolName(@ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId,
            @ApiParam("The tool name used to verify the release") @PathParam("toolName") String toolName,
            @ApiParam("Additional properties set for the verification") @Form(prefix = "props") Map<String, String> propMap) {
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
        Properties props = new Properties();
        if (propMap != null) {
            props.putAll(propMap);
        }
        Verification verification = ReleasesPage.runVerify(tool.getId(), release, props);
        return Response.ok(verification).build();
    }

    @ApiOperation(value = "Start to verify a release, returns ASAP.")
    @POST
    @Path("/verifyRelease/{releaseId}")
    public Response verifyRelease(@ApiParam("The release id want to verify") @PathParam("releaseId") String releaseId,
            Map<String, String> propMap) {
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
    @Path("/verify/{productName}/{releaseName}")
    public Response verifyWithZip(@ApiParam("The product name to be verified") @PathParam("productName") String productName,
            @ApiParam("The product name to be verified") @PathParam("releaseName") String releaseName,
            @ApiParam("Verify Parameters") @Form VerifyParam verifyParam) {
        if (productName == null || releaseName == null) {
            return Response.status(Status.BAD_REQUEST).
                    type(MediaType.TEXT_PLAIN_TYPE).build();
        }
        if (verifyParam == null) {
            return Response.status(Status.BAD_REQUEST).
                    type(MediaType.TEXT_PLAIN_TYPE).build();
        }
        PVTModel pvtModel = PVTApplication.getDAO().getPvtModel();
        List<VerifyTool> tools = new ArrayList<VerifyTool>();
        if (verifyParam.toolIds != null && verifyParam.toolIds.size() > 0) {
            for (String toolId: verifyParam.toolIds) {
                VerifyTool tool = pvtModel.getVerifyToolById(toolId);
                if (tool == null) {
                    return Response.status(Status.BAD_REQUEST).
                            type(MediaType.TEXT_PLAIN_TYPE).build();
                }
                tools.add(tool);
            }
        }
        Product prd = pvtModel.getProducts().stream().filter(p -> p.getName().equals(productName)).findAny().orElse(null);
        if (prd == null) {
            // create one if it does not exist.
            prd = new Product();
            prd.setName(productName);
            pvtModel.addProduct(prd);
        }
        List<Release> releases = pvtModel.getReleasesByProduct(prd.getId());
        Release release = releases.stream().filter(p -> p.getName().equals(releaseName)).findAny().orElse(null);
        if (release == null) {
            release = new Release();
            release.setCreateTime(System.currentTimeMillis());
            release.setName(releaseName);
            release.setProductId(prd.getId());
            pvtModel.addRelease(release);
        }
        if (verifyParam.zipUrls != null && verifyParam.zipUrls.size() > 0) {
            release.setDistributions(String.join("", verifyParam.zipUrls));
        }
        if (verifyParam.repoUrl != null && verifyParam.repoUrl.length() > 0) {
            release.setRepo(verifyParam.repoUrl);
        }
        //TODO remove the map between release and tools
        if (verifyParam.toolIds != null && verifyParam.toolIds.size() > 0) {
            release.setTools(verifyParam.toolIds);
        }
        PVTApplication.getDAO().persist();
        Properties props = verifyParam.mapAsProperties();
        List<Verification> verifications = new ArrayList<Verification>();
        if (tools.size() > 0) {
            for (VerifyTool tool: tools) {
                verifications.add(ReleasesPage.runVerify(tool.getId(), release, props));
            }
        }

        // start call back handler
        if (verifyParam.callback != null) {
            try {
                Executor.getJVMExecutor().execute(Execution.createJVMExecution("Release-CallBack-Tracker", new ExecutionRunnable() {

                    @Override
                    public void doRun() throws Exception {
                        while (verifications.stream().anyMatch(p -> p.getStatus().equals(Verification.Status.IN_PROGRESS)
                                || p.getStatus().equals(Verification.Status.NEW))) {
                            Thread.sleep(50);
                        }
                        setStatus(Execution.Status.SUCCEEDED);
                    }
                }), new CallBack() {
                    public void onTerminated(Execution execution) {
                        try {
                            callBack(verifyParam.callback, verifications);
                        } catch (Exception e) {
                            logger.warn("Error to send call back to:" + verifyParam.callback, e);
                        }
                    }

                });
            } catch (ExecutionException e) {
                logger.warn("Error on monitoring the verifications.", e);
            }
        }
        return Response.ok(verifications).build();
    }

    private void callBack(String callback, List<Verification> verifications) throws Exception {
        assert callback != null;
        List<VerificationMeta> metas = verifications.stream()
                .map(v -> new VerificationMeta()
                    .setStatus(v.getStatus().name())
                    .setWaiveComment(v.getWaiveComment()))
                    .collect(Collectors.toList());

        ClientRequest request = new ClientRequest(callback);
        request.accept(MediaType.TEXT_PLAIN);
        request.body(MediaType.APPLICATION_JSON_TYPE, metas);
        ClientResponse<?> response = request.post();
        int statusCode = response.getStatus();
        if (statusCode != 200) {
            logger.warn(String.format("Not expected status: %d from URL: %s", statusCode, callback));
        }
    };

    public static class VerifyParam {
        @ApiParam("The released archive urls of the release") @FormParam("zipUrl") List<String> zipUrls;
        @ApiParam("The maven repository url of the release") @FormParam("repoUrl") String repoUrl;
        @ApiParam("The tools used to execute the verification.[specify id]") @FormParam("toolId") List<String> toolIds;
        @ApiParam("The callback URL after the verification is finished") @FormParam("callback") String callback;
        @ApiParam("Additional properties set for the verification") @Form(prefix = "props") Map<String, StringValue> propMap;

        public Properties mapAsProperties() {
            Properties props = new Properties();
            if (propMap != null) {
                propMap.forEach((k, v) -> props.put(k, v.toString()));
            }
            return props;
        }
    }

    public static class StringValue {
        @FormParam("value") String value;

        @Override
        public String toString() {
            return value;
        }
    }
}
