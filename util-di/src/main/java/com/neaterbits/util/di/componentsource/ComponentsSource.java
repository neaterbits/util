package com.neaterbits.util.di.componentsource;

import java.io.IOException;

import com.neaterbits.util.di.Instantiation;

public interface ComponentsSource<COMPONENT_SPEC> {

    @FunctionalInterface
    interface ComponentSpecProcessor<COMPONENT_SPEC> {
        
        void onComponentSpec(
                COMPONENT_SPEC componentSpec,
                ComponentsSource<COMPONENT_SPEC> requirementSource,
                
                String type, String role, Object roleHint, Instantiation instantiation);
    }
    
    @FunctionalInterface
    interface RequirementProcessor {
        
        void onRequirement(String role, Object roleHint, String fieldName);
    }
    
    void scanForComponentSpecs(ComponentSpecProcessor<COMPONENT_SPEC> processor) throws IOException, ComponentsSourceException;
    
    void scanForRequirements(COMPONENT_SPEC componentSpec, RequirementProcessor processor);
}
