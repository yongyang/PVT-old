package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;
import org.jboss.pnc.pvt.model.Release;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class ReleaseEditPage extends ReleaseNewPage {

    public ReleaseEditPage(PageParameters pp) {
        this(pp, "PVT release has been be modified.");
    }

    public ReleaseEditPage(PageParameters pp, String info) {
        super(pp, info);
    }


    @Override
    public void doSubmit() {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        release.setProductId(productDropDownChoice.getModelObject().getId());
        release.setTools(toolCheckBoxMultipleChoice.getInputAsArray() != null ? Arrays.asList(toolCheckBoxMultipleChoice.getInputAsArray()) : Collections.emptyList());
        release.setUpdateTime(System.currentTimeMillis());
        dao.getPvtModel().updateRelease(release);
        dao.persist();
        setInfo("Release: " + release.getName() + " is Updated.");
        setResponsePage(ReleasesPage.class);
    }

    @Override
    public void doReset() {
        releaseForm.getModel().setObject(release);
    }

    public boolean doRemove() {
        PVTDataAccessObject dao = PVTApplication.getDAO();
        //check the release if has running verification
        boolean success = dao.getPvtModel().removeRelease(release);
        if(success) {
            dao.persist();
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        nameTextField.setEnabled(false);
        productDropDownChoice.setEnabled(false);
        removeButton.setVisible(true);
        //will call New release validator
    }

    public String getTitle() {
        return "Modify a Release";
    }

    @Override
    protected Release getRelease(PageParameters pp) {
        if (pp != null) {
            if (!pp.get("releaseId").isNull()) {
                PVTDataAccessObject dao = PVTApplication.getDAO();
                return dao.getPvtModel().getReleasebyId(pp.get("releaseId").toString());
            }
        }

        return null;
    }
}
