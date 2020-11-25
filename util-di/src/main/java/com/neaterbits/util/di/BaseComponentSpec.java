package com.neaterbits.util.di;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
abstract class BaseComponentSpec {

    private final Object roleHint;
    private final Instantiation instantiation;

    BaseComponentSpec(Object roleHint, Instantiation instantiation) {

        Objects.requireNonNull(instantiation);
        
        this.roleHint = roleHint;
        this.instantiation = instantiation;
    }

    final Object getRoleHint() {
        return roleHint;
    }

    final Instantiation getInstantiation() {
        return instantiation;
    }
}
