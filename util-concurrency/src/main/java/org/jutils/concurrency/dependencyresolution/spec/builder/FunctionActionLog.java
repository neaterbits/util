package org.jutils.concurrency.dependencyresolution.spec.builder;

public final class FunctionActionLog extends ActionLog {

    public static final FunctionActionLog OK = new FunctionActionLog(null);
    
    private final Throwable exception;

    public FunctionActionLog(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
