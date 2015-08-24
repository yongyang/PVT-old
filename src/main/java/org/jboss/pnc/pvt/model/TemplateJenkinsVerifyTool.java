package org.jboss.pnc.pvt.model;

import java.io.Serializable;

/**
 * A verify tool to call jenkins server with config.xml job template provided
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class TemplateJenkinsVerifyTool extends VerifyTool {
    
    private static final long serialVersionUID = -7165679900379794100L;

    private String jenkinsConfigXML;

    public String getJenkinsConfigXML() {
        return jenkinsConfigXML;
    }

    public void setJenkinsConfigXML(String jenkinsConfigXML) {
        this.jenkinsConfigXML = jenkinsConfigXML;
    }

    @Override
    public Verification verify(VerifyParameter param) {
        return null;
    }
}
