package org.jboss.pnc.pvt.model;

/**
 * A verify tool to call jenkins server with config.xml job template provided
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class TemplateJenkinsVerifyTool extends VerifyTool {

    protected String jenkinsConfigXML;

    public String getJenkinsConfigXML() {
        return jenkinsConfigXML;
    }

    public void setJenkinsConfigXML(String jenkinsConfigXML) {
        this.jenkinsConfigXML = jenkinsConfigXML;
    }

    @Override
    protected VerifyResult verify(VerifyParameter param) {
        //TODO: call Jenkins Executor
        return null;
    }
}
