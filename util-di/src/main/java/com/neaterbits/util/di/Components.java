package com.neaterbits.util.di;

import java.util.List;
import java.util.Map;

public interface Components {
    
    public static Components makeInstance() {
        
        return new ComponentsImpl();
    }
    
    Object lookupObjectByType(String type);

    Object lookupObject(String role, String hint);

    List<Object> lookupObjectList(String role);
    
    Map<String, Object> lookupObjectMap(String role);
   
    void addComponentObject(Object component, String role, String hint);

    default boolean hasComponent(String role) {
        
        final List<Object> objects = lookupObjectList(role);
        
        return objects != null && !objects.isEmpty();
    }

    default boolean hasComponent(String role, String hint) {
        
        return lookupObject(role, hint) != null;
    }

    default boolean hasComponent(Class<?> role) {
        
        return lookupObject(role.getName(), null) != null;
    }
}
