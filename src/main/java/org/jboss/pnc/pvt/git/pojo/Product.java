package org.jboss.pnc.pvt.git.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping product configuration in gitdb
 *
 * Created by yyang on 4/16/15.
 */
public class Product {

    private String name;
    private String version;
    private String release;

    private List<Job> jobs = new ArrayList<>();


}
