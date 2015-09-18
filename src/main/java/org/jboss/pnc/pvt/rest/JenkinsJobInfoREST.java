package org.jboss.pnc.pvt.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dom4j.DocumentException;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.execution.JenkinsConfiguration;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob;
import org.jboss.pnc.pvt.execution.ParamJenkinsJob.SerializableStringParam;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;

@Path("/jenkinsInfo")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsJobInfoREST {

    @Path("/{jobId}")
    @GET
    public Response getJenkinsInfoByJobId(@PathParam("jobId") String jobId) {
        try {
            JenkinsServer jServer = Executor.getJenkinsServer(JenkinsConfiguration.defaultJenkinsProps());
            JobWithDetails job = jServer.getJob(jobId);
            if (job == null) {
                JenkinsJobInfo jobInfo = new JenkinsJobInfo();
                jobInfo.setFeedback(feedbackHTML("Jenkins job: " + jobId + " does not exist."));
                return Response.ok(jobInfo).build();
            } else {
                String jobXML = jServer.getJobXml(jobId);
                return getJenkinsInfoByJobContent(jobXML);
            }
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN_TYPE).entity(e.getMessage()).build();
        }
    }

    @Path("/content/xml")
    @POST
    public Response getJenkinsInfoByJobContent(@FormParam("jobContent") String jobContent) {
        JenkinsJobInfo jobInfo = new JenkinsJobInfo();
        try {
            ParamJenkinsJob job = new ParamJenkinsJob(jobContent);
            jobInfo.setArchiver(archiverHTML(job.getArchiver()));
            jobInfo.setStringparams(stringParamsHTML(job.getStringParams()));
        } catch (DocumentException e) {
            jobInfo.setFeedback(feedbackHTML("Jenkins XML content is not valid."));
        }
        return Response.ok(jobInfo).build();
    }

    private String feedbackHTML(String feedback) {
        StringBuilder sb = new StringBuilder();
        if (feedback != null && feedback.length() > 0) {
            sb.append("<li class=\"feedbackPanelERROR\">");
            sb.append("<span class=\"feedbackPanelERROR\">" + feedback + "</span>");
            sb.append("</li>");
        }
        return sb.toString();
    }

    private String archiverHTML(String archiver) {
        StringBuilder sb = new StringBuilder();
        if (archiver != null && archiver.length() > 0) {
            sb.append(archiver);
        } else {
            sb.append("No Archiver is specified.");
        }
        return sb.toString();
    }

    private String stringParamsHTML(List<SerializableStringParam> stringParams) {
        StringBuilder sb = new StringBuilder();
        if (stringParams != null && stringParams.size() > 0) {
            for (SerializableStringParam sp: stringParams) {
                sb.append("<tr class=\"upper\">");
                sb.append("<td class=\"norightpad first\">" + sp.getName() + "</td>\n");
                sb.append("<td class=\"norightpad first\">" + sp.getDefaultValue() + "</td>\n");
                sb.append("<td class=\"norightpad first\">" + sp.getDescription() + "</td>\n");
                sb.append("</tr>\n");
            }
        }
        return sb.toString();
    }

    @JsonAutoDetect
    public static class JenkinsJobInfo {

        private String feedback;

        private String archiver;

        private String stringparams;

        /**
         * @return the feedback
         */
        public String getFeedback() {
            return feedback;
        }

        /**
         * @param feedback the feedback to set
         */
        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        /**
         * @return the archiver
         */
        public String getArchiver() {
            return archiver;
        }

        /**
         * @param archiver the archiver to set
         */
        public void setArchiver(String archiver) {
            this.archiver = archiver;
        }

        /**
         * @return the stringparams
         */
        public String getStringparams() {
            return stringparams;
        }

        /**
         * @param stringparams the stringparams to set
         */
        public void setStringparams(String stringparams) {
            this.stringparams = stringparams;
        }

    }
}