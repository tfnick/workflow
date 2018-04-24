package org.neuro4j.workflow.utils;

import org.neuro4j.workflow.common.ExpressionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEvalUtil {


    private static ScriptEngineManager factory = new ScriptEngineManager();
    private static ScriptEngine engine = null;

    static {
        engine = factory.getEngineByName("JavaScript");
    }


    public static  Object eval(String expression) throws ExpressionException {
        if (expression == null || "".equals(expression)) {
            throw new ExpressionException("expression not allow null");
        }
        Object result = null;
        try {
            result = engine.eval(expression);
        } catch (ScriptException e) {
            throw new ExpressionException("expression eval exception" + e.getMessage());
        }

        return result;
    }

    public static void main(String[] args) throws ExpressionException{
        System.out.println(ScriptEvalUtil.eval("true == true && false == false"));
        System.out.println(ScriptEvalUtil.eval("1 + 3 * (1 + 4) > 10"));
        System.out.println(ScriptEvalUtil.eval("15 + 5 > 19 && 4 / 2 == "));
    }
}
