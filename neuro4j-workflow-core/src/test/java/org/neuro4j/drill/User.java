package org.neuro4j.drill;

import java.util.Map;

public class User {
    private String name;

    private Map<String,Object> attrs ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }


}
