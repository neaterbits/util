package org.jutils.di;

import java.util.Objects;

abstract class TypeResolver {

    private final ClassAwareComponentSpecs specs;

    public TypeResolver(CollectedComponentSpecs specs, ClassLoader classLoader)
                throws ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {

        Objects.requireNonNull(specs);
        Objects.requireNonNull(classLoader);
        
        this.specs = new ClassAwareComponentSpecs(specs, classLoader);
    }
    
    final ClassAwareComponentSpec getByType(Class<?> type) {
        return specs.getByType(type);
    }

    final ClassAwareComponentSpec resolve(Class<?> role, Object roleHint) {
        return specs.resolve(role, roleHint);
    }

    final ClassAwareComponentSpec resolveRequirement(ClassAwareComponentRequirement requirement) {
        
        return specs.resolve(requirement);
    }
}
