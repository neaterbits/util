package com.neaterbits.util.di;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Component specs as collected from component sources, without any Class information, i.e. class loader agnostic, all types
 * are String class names.  
 *
 */

final class CollectedComponentSpecs {

    private final List<CollectedComponentSpec> specs;
    
    private final Map<String, CollectedComponentSpec> byType;
    
    CollectedComponentSpecs() {
        this.specs = new ArrayList<>();
        
        this.byType = new HashMap<>();
    }

    CollectedComponentSpec addSpec(URL source, String type, String role, Object roleHint, Instantiation instantiation) {
        
        Objects.requireNonNull(type);
        Objects.requireNonNull(instantiation);
        
        if (byType.containsKey(type)) {
            throw new IllegalArgumentException("Already contains type " + type);
        }
        
        final CollectedComponentSpec spec = new CollectedComponentSpec(source, type, role, roleHint, instantiation);
        
        specs.add(spec);
        
        return spec;
    }
    
    List<CollectedComponentSpec> getAll() {
        return specs;
    }
    
    CollectedComponentSpec getByType(String type) {
        
        Objects.requireNonNull(type);
        
        return byType.get(type);
    }
    
    CollectedComponentSpec getByRequirement(CollectedComponentRequirement requirement) {

        return specs.stream()
                .filter(spec -> requirement.getRole().equals(spec.getRole()) && requirement.getRoleHint().equals(spec.getRoleHint()))
                .findFirst()
                .orElse(null);
    }
}
