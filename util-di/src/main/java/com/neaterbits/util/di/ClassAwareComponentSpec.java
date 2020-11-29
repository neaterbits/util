package com.neaterbits.util.di;

import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import com.neaterbits.util.coll.Coll;

@Immutable
final class ClassAwareComponentSpec extends BaseComponentSpec {

    private final Class<?> type;
    private final List<Class<?>> roles;
    private final List<ClassAwareComponentRequirement> requirements;
    
    ClassAwareComponentSpec(
            CollectedComponentSpec collectedComponentSpec,
            Class<?> type,
            List<Class<?>> roles,
            List<ClassAwareComponentRequirement> requirements) {
        super(collectedComponentSpec);
        
        Objects.requireNonNull(type);
        Objects.requireNonNull(roles);
        
        if (roles.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        this.type = type;
        this.roles = roles;
        this.requirements = Coll.safeImmutable(requirements);
    }

    Class<?> getType() {
        return type;
    }

    List<Class<?>> getRoles() {
        return roles;
    }

    List<ClassAwareComponentRequirement> getRequirements() {
        return requirements;
    }

    boolean matches(Class<?> role, Object roleHint) {
        
        Objects.requireNonNull(role);
        
        return roles.contains(role)
                && Objects.equals(getRoleHint(), roleHint);
    }

    boolean matches(ClassAwareComponentRequirement requirement) {
        return matches(requirement.getRole(), requirement.getRoleHint());
    }
}
