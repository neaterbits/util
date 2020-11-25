package com.neaterbits.util.di;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class CollectedComponentSpec extends BaseComponentSpec {
    
    private final String type;
    private final String role; // may be null if not specified, roles will be found from type
    
    private final List<CollectedComponentRequirement> requirements;

    CollectedComponentSpec(String type, String role, Object roleHint, Instantiation instantiation) {
        
        super(roleHint, instantiation);
        
        Objects.requireNonNull(type);
        
        this.type = type;
        this.role = role;
        
        this.requirements = new ArrayList<>();
    }
    
    void addRequirement(String role, Object roleHint, String fieldName) {

        requirements.add(new CollectedComponentRequirement(role, roleHint, fieldName));
    }

    String getType() {
        return type;
    }

    String getRole() {
        return role;
    }

    List<CollectedComponentRequirement> getRequirements() {
        return requirements;
    }
}