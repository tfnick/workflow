package org.neuro4j.drill;

import org.junit.Assert;
import org.junit.Test;
import org.neuro4j.workflow.utils.MapDotUtil;

import java.util.HashMap;
import java.util.Map;

public class DrillTest {

    @Test
    public void testDrill(){
        User u = new User();
        u.setName("wangbing");
        Map<String, Object> attrs = new HashMap<>();
        u.setAttrs(attrs);
        attrs.put("age", 10);
        attrs.put("sex", "Male");

        Assert.assertEquals("wangbing", MapDotUtil.drillBean(u,"name"));
        Assert.assertEquals(10, MapDotUtil.drillBean(u,"attrs.age"));
        Assert.assertEquals(null, MapDotUtil.drillBean(u,"attrs.unKnow"));
    }
}
