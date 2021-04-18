package org.jutils.di;

public final class UnknownFieldException extends ResolveException {

    private static final long serialVersionUID = 1L;

    UnknownFieldException(String message) {
        super(message);
    }
}
