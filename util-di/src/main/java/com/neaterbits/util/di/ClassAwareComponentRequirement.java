package com.neaterbits.util.di;

import java.lang.reflect.Field;
import java.util.Objects;

final class ClassAwareComponentRequirement extends BaseComponentRequirement {

    private final Class<?> role;
    private final Field field;
    
    ClassAwareComponentRequirement(Class<?> role, Object roleHint, Field field) {
        super(roleHint);
        
        Objects.requireNonNull(role);
        
        this.role = role;
        this.field = field;
    }

    Class<?> getRole() {
        return role;
    }

    Field getField() {
        return field;
    }
}
