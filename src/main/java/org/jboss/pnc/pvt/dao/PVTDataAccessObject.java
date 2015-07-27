package org.jboss.pnc.pvt.dao;

import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public abstract class PVTDataAccessObject {

    public abstract void load();

    public abstract void persist();

    public abstract PVTModel getPvtModel();

    public abstract Product newProduct(String name);
}
