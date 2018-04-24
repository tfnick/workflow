package org.neuro4j.workflow.common;

public class ExpressionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 100000000000L;

    public ExpressionException(String msg) {
        super(msg);
    }

    public ExpressionException(String string, Exception e1) {
        super(string, e1);
    }

    public ExpressionException(Throwable cause) {
        super(cause);
    }

}
