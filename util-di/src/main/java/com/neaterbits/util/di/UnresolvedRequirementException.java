package com.neaterbits.util.di;

public final class UnresolvedRequirementException extends ResolveException {

    private static final long serialVersionUID = 1L;

    UnresolvedRequirementException(String message) {
        super(message);
    }
}
