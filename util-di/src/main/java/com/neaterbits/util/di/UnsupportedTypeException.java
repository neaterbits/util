package com.neaterbits.util.di;

final class UnsupportedTypeException extends ResolveException {

    private static final long serialVersionUID = 1L;

    public UnsupportedTypeException(String message) {
        super(message);
    }
}
