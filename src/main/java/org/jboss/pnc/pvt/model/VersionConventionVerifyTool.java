package org.jboss.pnc.pvt.model;


/**
 * A verify tool to check the version convention, ex: the jars should have -rehdat-x suffix in version
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class VersionConventionVerifyTool extends VerifyTool {

    private static final long serialVersionUID = 8002107449547514235L;

    public static final String LABEL = "Version Convention Check";


    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.model.VerifyTool#getLabel()
     */
    @Override
    public String getLabel() {
        return LABEL;
    }

    /* (non-Javadoc)
     * @see org.jboss.pnc.pvt.model.VerifyTool#getPageVariant()
     */
    @Override
    public String getPageVariant() {
        return "versionconvention";
    }

    @Override
    public UseType getUseType() {
        return UseType.STATIC; // always used for static package analysis
    }

    @Override
    protected <T> VerifyResult<T> verify(VerifyParameter param) {
        //TODO:
        return null;
    }
}
