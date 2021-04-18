package org.jutils.di;

import java.net.URL;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
abstract class BaseComponentSpec {

    private final URL source;

    private final Object roleHint;
    private final Instantiation instantiation;

    BaseComponentSpec(URL source, Object roleHint, Instantiation instantiation) {

        Objects.requireNonNull(instantiation);
        
        this.source = source;
        
        this.roleHint = roleHint;
        this.instantiation = instantiation;
    }
    
    BaseComponentSpec(BaseComponentSpec other) {
        this(other.source, other.roleHint, other.instantiation);
    }

    final URL getSource() {
        return source;
    }

    final Object getRoleHint() {
        return roleHint;
    }

    final Instantiation getInstantiation() {
        return instantiation;
    }
}
