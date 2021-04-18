package org.jutils.di;

abstract class ResolveException extends Exception {

    private static final long serialVersionUID = 1L;

    ResolveException(String message) {
        super(message);
    }
}
