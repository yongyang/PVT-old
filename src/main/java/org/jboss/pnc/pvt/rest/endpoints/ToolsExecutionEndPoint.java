package org.jboss.pnc.pvt.rest.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;
import org.jboss.pnc.pvt.execution.JobMapper;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/tools", description = "Execute Tools")
@Path("/tools")
@Produces(MediaType.APPLICATION_JSON)
public class ToolsExecutionEndPoint {

    private static final Logger logger = Logger.getLogger(ToolsExecutionEndPoint.class);

    private final HttpClient httpClient = HttpClientBuilder.create().build();

    public ToolsExecutionEndPoint() {
    }

    @ApiOperation(value = "Start a tool execution(a Jenkins execution tool), returns ASAP.")
    @POST
    @Path("/start/{productName}/{version}/{toolName}")
    public Response start(@ApiParam("The product name used to run against") @PathParam("productName") String productName,
            @ApiParam("The product version used to run against") @PathParam("version") String version,
            @ApiParam("The tool name used to run") @PathParam("toolName") String toolName) {
        String jobName = JobMapper.DEFAULT.getJobName(productName, version, toolName);
        String jobContent = JobMapper.DEFAULT.getJobXMLContent(toolName);
        if (jobContent == null || jobContent.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("Can't know job content of tool: " + toolName).build();
        }
        Execution execution = Execution.createJenkinsExecution(jobName, jobContent, null);
        try {
            JenkinsConfiguration config = JenkinsConfiguration.defaultJenkinsProps();
            config.setCreateIfJobMissing(true); // create if missing
            config.setOverrideJob(true);
            Executor.getJenkinsExecutor(config).execute(execution, null);
        } catch (ExecutionException | IOException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        return Response.ok(execution).build();
    }

    @ApiOperation(value = "Start a tool execution(a Jenkins execution tool). Waits until the Jenkins Job is fished.")
    @POST
    @Path("/startWait/{productName}/{version}/{toolName}")
    public Response startAndWait(@ApiParam("The product name used to run against") @PathParam("productName") String productName,
            @ApiParam("The product version used to run against") @PathParam("version") String version,
            @ApiParam("The tool name used to run") @PathParam("toolName") String toolName) {
        String jobName = JobMapper.DEFAULT.getJobName(productName, version, toolName);
        String jobContent = JobMapper.DEFAULT.getJobXMLContent(toolName);
        if (jobContent == null || jobContent.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("Can't know job content of tool: " + toolName).build();
        }
        Execution execution = Execution.createJenkinsExecution(jobName, jobContent, null);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CallBack callBack = new CallBack() {
            @Override
            public void onTerminated(Execution execution) {
                countDownLatch.countDown();
            }
        };
        try {
            JenkinsConfiguration config = JenkinsConfiguration.defaultJenkinsProps();
            config.setCreateIfJobMissing(true); // create if missing
            config.setOverrideJob(true);
            Executor.getJenkinsExecutor(config).execute(execution, callBack);
        } catch (ExecutionException | IOException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            ;
        }
        return Response.ok(execution).build();
    }

    @ApiOperation(value = "Start a tool execution(a Jenkins execution tool). Returns ASAP. An URL for the callback when the Jenkins job is completed or failed.")
    @POST
    @Path("/startWithCallback/{productName}/{version}/{toolName}")
    public Response startWithCallback(
            @ApiParam("The product name used to run against") @PathParam("productName") String productName,
            @ApiParam("The product version used to run against") @PathParam("version") String version,
            @ApiParam("The tool name used to run") @PathParam("toolName") String toolName,
            @ApiParam("The Callback URL used to send response to using POST method.") @QueryParam("callBackURL") String callBackURL) {

        String jobName = JobMapper.DEFAULT.getJobName(productName, version, toolName);
        String jobContent = JobMapper.DEFAULT.getJobXMLContent(toolName);
        if (jobContent == null || jobContent.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("Can't know job content of tool: " + toolName).build();
        }
        Execution execution = Execution.createJenkinsExecution(jobName, jobContent, null);
        final CallBack callBack = new CallBack() {
            @Override
            public void onStatus(Execution execution) {
                if (execution.getStatus().equals(Execution.Status.FAILED)
                        || execution.getStatus().equals(Execution.Status.SUCCEEDED)) {
                    try {
                        logger.debug("Call back to: " + callBackURL);
                        HttpPost post = new HttpPost(callBackURL);
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("Status", execution.getStatus().name())); // TODO adds more
                                                                                                            // params ??

                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        HttpResponse resp = httpClient.execute(post);
                        int statusCode = resp.getStatusLine().getStatusCode();
                        if (statusCode != HttpStatus.SC_OK) {
                            logger.warn(String.format("Not expected status: %d from URL: %s", statusCode, callBackURL));
                        }
                    } catch (IOException e) {
                        logger.warn("Can't call back: " + callBackURL, e);
                    }
                }
            }
        };
        try {
            JenkinsConfiguration config = JenkinsConfiguration.defaultJenkinsProps();
            config.setCreateIfJobMissing(true); // create if missing
            config.setOverrideJob(true);
            Executor.getJenkinsExecutor(config).execute(execution, callBack);
        } catch (ExecutionException| IOException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        return Response.ok(execution).build();
    }

}
