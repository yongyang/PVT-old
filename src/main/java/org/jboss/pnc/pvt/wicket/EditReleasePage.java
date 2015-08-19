package org.jboss.pnc.pvt.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.pnc.pvt.dao.PVTDataAccessObject;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class EditReleasePage extends NewReleasePage {

    public EditReleasePage(PageParameters pp) {
        this(pp, "PVT release has been be modified.");
    }

    public EditReleasePage(PageParameters pp, String info) {
        super(pp, info);
    }

    @Override
    public void doSubmit() {
        PVTDataAccessObject dao = ((PVTApplication) Application.get()).getDAO();
        release.setProductId(productDropDownChoice.getModelObject().getId());
        dao.getPvtModel().updateRelease(release);
        dao.persist();
        setInfo("Release: " + release.getName() + " is Updated.");
    }

    @Override
    public void doReset() {
        releaseForm.getModel().setObject(release);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        nameTextField.setEnabled(false);
        removeButton.setVisible(true);
    }
}
