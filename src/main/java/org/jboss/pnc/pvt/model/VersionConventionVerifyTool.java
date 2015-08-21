package org.jboss.pnc.pvt.model;


import java.io.Serializable;

/**
 * A verify tool to check the version convention, ex: the jars should have -rehdat-x suffix in version
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VersionConventionVerifyTool extends VerifyTool {

    private static final long serialVersionUID = 8002107449547514235L;

    private String conventionRegexp;

    public static final String LABEL = "Version Convention Check";


    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.model.VerifyTool#getLabel()
     */
    @Override
    public String getLabel() {
        return LABEL;
    }

    public String getConventionRegexp() {
        return conventionRegexp;
    }

    public void setConventionRegexp(String conventionRegexp) {
        this.conventionRegexp = conventionRegexp;
    }

    @Override
    protected <T extends Serializable> Verification<T> verify(VerifyParameter param) {
        //TODO: call Jenkins Executor
        return null;
    }
}
