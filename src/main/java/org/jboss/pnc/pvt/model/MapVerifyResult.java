package org.jboss.pnc.pvt.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class MapVerifyResult extends VerifyResult<Map> {

    private Map<Object, Object> resultMap = new HashMap<>();

    public void addResultItem(Object key, Object value){
        resultMap.put(key, value);
    }

    @Override
    public Map getResult() {
        return resultMap;
    }
}
