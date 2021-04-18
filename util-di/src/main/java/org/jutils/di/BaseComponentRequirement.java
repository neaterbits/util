package org.jutils.di;

import javax.annotation.concurrent.Immutable;

@Immutable
abstract class BaseComponentRequirement {

    private final Object roleHint;

    BaseComponentRequirement(Object roleHint) {
        this.roleHint = roleHint;
    }

    final Object getRoleHint() {
        return roleHint;
    }
}
