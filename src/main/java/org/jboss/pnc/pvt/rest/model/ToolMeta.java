package org.jboss.pnc.pvt.rest.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A POJO represents Tool's name and id which is easy to use for REST api.
 *
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
@JsonAutoDetect
public class ToolMeta implements Serializable {

    private static final long serialVersionUID = 4800460387333614890L;

    private String id;
    private String name;
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public ToolMeta setId(String id) {
        this.id = id;
        return this;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public ToolMeta setName(String name) {
        this.name = name;
        return this;
    }

}
