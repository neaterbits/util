package com.neaterbits.util.di;

final class UnresolvedRequirementException extends ResolveException {

    private static final long serialVersionUID = 1L;

    UnresolvedRequirementException(String message) {
        super(message);
    }
}
