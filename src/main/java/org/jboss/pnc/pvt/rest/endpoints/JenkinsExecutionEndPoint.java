package org.jboss.pnc.pvt.rest.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/jenkins", description = "Execute Jenkins Jobs")
@Path("/jenkins")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsExecutionEndPoint {

    private static final Logger logger = Logger.getLogger(JenkinsExecutionEndPoint.class);

    private final HttpClient httpClient = new DefaultHttpClient();

    public JenkinsExecutionEndPoint() {
    }

    @ApiOperation(value = "Start a Jenkins Job, returns as soon as possible with a link to the Jenkins build.")
    @POST
    @Path("/start/{jobName}")
    public Response start(@ApiParam("The Jenkins Job Name. Make sure the job existed already.") @PathParam("jobName") String jobName)
             {
        Execution execution = Execution.createJenkinsExecution(jobName);
        try {
            Executor.getJenkinsExecutor().execute(execution);
        } catch (ExecutionException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        return Response.ok(execution).build();
    }

    @ApiOperation(value = "Start a Jenkins Job, returns ASAP. An URL for the callback when the Jenkins job is completed or failed.")
    @POST
    @Path("/startWithCallback/{jobName}")
    public Response startWithCallback(
            @ApiParam("The Jenkins Job Name. Make sure the job existed already.") @PathParam("jobName") String jobName,
            @ApiParam("The Callback URL used to send response to using POST method.") @QueryParam("callBackURL") String callBackURL)
            {

        Execution execution = Execution.createJenkinsExecution(jobName);
        execution.addCallBack(new Execution.CallBack() {

            @Override
            public void onStatus(Execution execution) {
                if (execution.getStatus().equals(Execution.Status.FAILED)
                        || execution.getStatus().equals(Execution.Status.SUCCEEDED)) {
                    try {
                        logger.info("Call back to: " + callBackURL);
                        HttpPost post = new HttpPost(callBackURL);
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("Status", execution.getStatus().name())); //TODO more params to be sent ??

                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        HttpResponse resp = httpClient.execute(post);
                        int statusCode = resp.getStatusLine().getStatusCode();
                        if (statusCode != HttpStatus.SC_OK) {
                            logger.warn(String.format("Not expected status: %d from URL: %s", statusCode, callBackURL) );
                        }
                    } catch (IOException e) {
                        logger.warn("Can't call back: " + callBackURL, e);
                    }
                }
            }

            @Override
            public void onLogChanged(Execution execution) {
                logger.info("Log is: " + execution.getLog());
            }
        });
        try {
            Executor.getJenkinsExecutor().execute(execution);
        } catch (ExecutionException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        return Response.ok(execution).build();
    }

    @ApiOperation(value = "Start a Jenkins Job with specified Jenkins configuration, returns as soon as possible with a link to the Jenkins build.")
    @POST
    @Path("/startJenkins/{jobName}")
    public Response startJenkins(
            @ApiParam("The Jenkins Job Name. Make sure the job existed already in the specified Jenkins Server.") @PathParam("jobName") String jobName,
            @ApiParam("The Jenkins Server URL. Required.") @QueryParam("jenkinsURL") String jenkinsURL,
            @ApiParam("The Jenkins Username. Optional.") @QueryParam("username") String jenkinsUserName,
            @ApiParam("The Jenkins Password. Optional.") @QueryParam("password") String jenkinsPassword)
            {

        if (jenkinsURL == null || jenkinsURL.trim().length() == 0) {
            return Response.status(Status.BAD_REQUEST).entity("Please specify JenkinsURL.").build();
        }
        final JenkinsConfiguration jenkinsConfig = new JenkinsConfiguration();
        jenkinsConfig.setUrl(jenkinsURL);
        jenkinsConfig.setUsername(jenkinsUserName);
        jenkinsConfig.setPassword(jenkinsPassword);

        Execution execution = Execution.createJenkinsExecution(jobName);
        try {
            Executor.getJenkinsExecutor(jenkinsConfig).execute(execution);
        } catch (ExecutionException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }

        return Response.ok(execution).build();
    }

}
