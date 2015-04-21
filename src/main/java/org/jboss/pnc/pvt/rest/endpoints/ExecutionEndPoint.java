package org.jboss.pnc.pvt.rest.endpoints;

import java.util.concurrent.Future;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.ExecutorFactory;
import org.jboss.pnc.pvt.execution.Stage;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/execute", description = "Execute a Jenkins Job")
@Path("/execute")
@Produces(MediaType.APPLICATION_JSON)
public class ExecutionEndPoint {

    public ExecutionEndPoint() {
    }

    @ApiOperation(value = "Start a Jenkins Job")
    @POST
    @Path("/{jobName}")
    public Response execute(@ApiParam("Jenkins Job Name") @PathParam("jobName") String jobName)
            throws ExecutionException {
        Executor executor = ExecutorFactory.getExecutor();
        Execution execution = executor.execute(jobName);
        return Response.ok(execution).build();
    }
    
    @ApiOperation(value = "Start a Jenkins Job and wait until it starts")
    @POST
    @Path("/waitStart/{jobName}")
    public Response executeAndWaitStart(@ApiParam("Jenkins Job Name") @PathParam("jobName") String jobName)
            throws ExecutionException {
        Executor executor = ExecutorFactory.getExecutor();
        Future<Execution> future = executor.execute(jobName, Stage.Running);
        try {
            return Response.ok(future.get()).build();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Can't execute the job", e);
        }
    }
    
    @ApiOperation(value = "Start a Jenkins Job and wait until it completes")
    @POST
    @Path("/waitComplete/{jobName}")
    public Response startVerificationWaitFinish(@ApiParam("Jenkins Job Name") @PathParam("jobName") String jobName)
            throws ExecutionException {
        Executor executor = ExecutorFactory.getExecutor();
        Future<Execution> future = executor.execute(jobName, Stage.Complet);
        try {
            return Response.ok(future.get()).build();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new ExecutionException("Can't execute the job", e);
        }
    }
}
