package org.jutils.di;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class ComponentsImpl implements Components {

    private static class Component {
        private final String hint;
        private final Object instance;
        
        Component(String hint, Object instance) {

            Objects.requireNonNull(instance);
            
            this.hint = hint;
            this.instance = instance;
        }
    }
    
    interface Instantiator {
        
        Object create();
    }

    private final Map<String, List<Component>> components;
    private final Map<String, Component> componentsByType;
    
    public ComponentsImpl() {
        this.components = new HashMap<>();
        this.componentsByType = new HashMap<>();
    }

    Object getOrCreate(String type, Instantiator instantiator) {

        Object instance;
        Component component;
        
        synchronized (this) {
            component = componentsByType.get(type);
            
            if (component == null) {
                instance = instantiator.create();
                
                if (instance == null) {
                    throw new IllegalStateException();
                }
                
                componentsByType.put(type, new Component(null, instance));
            }
            else {
                instance = component.instance;
            }
        }
        
        return instance;
    }

    private List<Component> lookupComponents(String role) {
        
        Objects.requireNonNull(role);
        
        return components.get(role);
    }
    
    @Override
    public Object lookupObjectByType(String type) {

        Objects.requireNonNull(type);
        
        final Object object;

        synchronized (this) {
            
            final Component c = componentsByType.get(type);
            
            object = c != null ? c.instance : null;
        }
        
        return object;
    }

    @Override
    public Object lookupObject(String role, String hint) {
        
        Objects.requireNonNull(role);
        Objects.requireNonNull(hint);
        
        final Object object;
        
        synchronized (this) {

            final List<Component> list = lookupComponents(role);
            
            object = list == null || list.isEmpty()
                    ? null
                    : list.stream()
                        .filter(c -> c.hint.equals(hint))
                        .findFirst()
                        .map(c -> c.instance)
                        .orElse(null);
        }
        
        return object;
    }

    @Override
    public List<Object> lookupObjectList(String role) {

        final List<Object> objectList;
        
        synchronized (this) {
            final List<Component> list = lookupComponents(role);
    
            objectList = list == null || list.isEmpty()
                    ? null
                    : list.stream()
                        .map(c -> c.instance)
                        .collect(Collectors.toUnmodifiableList());
        }
        
        return objectList;
    }

    @Override
    public Map<String, Object> lookupObjectMap(String role) {

        final Map<String, Object> map;
        
        synchronized (this) {
            final List<Component> list = lookupComponents(role);
    
            map = list == null || list.isEmpty()
                    ? null
                    : list.stream()
                        .collect(Collectors.toUnmodifiableMap(c -> c.hint, c -> c.instance));
        }

        return map;
    }

    @Override
    public void addComponentObject(Object component, String role, String hint) {

        Objects.requireNonNull(component);
        Objects.requireNonNull(role);
        Objects.requireNonNull(hint);
        
        synchronized (this) {
            if (hasComponent(role, hint)) {
                throw new IllegalStateException();
            }
            
            List<Component> list = components.get(role);
            
            if (list == null) {
                list = new ArrayList<>();
                
                components.put(role, list);
            }

            list.add(new Component(hint, component));
        }
    }
}
