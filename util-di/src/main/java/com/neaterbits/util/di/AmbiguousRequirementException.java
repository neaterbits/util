package com.neaterbits.util.di;

final class AmbiguousRequirementException extends ResolveException {

    private static final long serialVersionUID = 1L;

    AmbiguousRequirementException(String message) {
        super(message);
    }
}
