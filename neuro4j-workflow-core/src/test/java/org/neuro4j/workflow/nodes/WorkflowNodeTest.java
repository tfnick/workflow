package org.neuro4j.workflow.nodes;

import org.junit.Test;
import org.neuro4j.workflow.FlowContext;
import org.neuro4j.workflow.node.WorkflowNode;

import java.util.Map;

public class WorkflowNodeTest {

    @Test
    public void testParamMapping(){
        WorkflowNode node = new WorkflowNode("test", "uuid");

        FlowContext ctx = new FlowContext();
        ctx.put("age",14);
        ctx.put("name","luoli");
        node.evaluateParameterValue("#{'age':${age},'multiAge':${age},'name':${name},'nullInt':${nullInt},'nullStr':${nullStr}}", "params", ctx);

        assert ctx.get("params") instanceof Map;
    }
}
