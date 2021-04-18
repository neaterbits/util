package org.jutils.di;

import java.lang.reflect.InvocationTargetException;

import org.jutils.reflection.FieldUtil;

public final class Resolver extends TypeResolver {
    
    private final ComponentsImpl components;

    Resolver(CollectedComponentSpecs specs, ClassLoader classLoader)throws ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        super(specs, classLoader);
        
        this.components = new ComponentsImpl();
    }
    
    public Components getComponents() {
        return components;
    }

    public <T> T instantiate(Class<T> type) {
        
        final ClassAwareComponentSpec spec = getByType(type);

        if (spec == null) {
            throw new IllegalArgumentException("No spec for type '" + type + "'");
        }
        
        return instantiate(spec);
    }

    public <T> T instantiate(Class<T> role, Object roleHint) {
        
        final ClassAwareComponentSpec spec = resolve(role, roleHint);

        if (spec == null) {
            throw new IllegalArgumentException("No spec for role '" + role.getName() + "', role hint '" + roleHint + "'");
        }
        
        return instantiate(spec);
    }

    private <T> T instantiate(ClassAwareComponentSpec spec) {
        
        T instance;
        
        switch (spec.getInstantiation()) {
        
        case SINGLETON:
            
            @SuppressWarnings("unchecked")
            final T singleton = (T)components.getOrCreate(spec.getType().getName(), () -> makeInstance(spec));
            
            instance = singleton;
            break;
            
        case PROTOTYPE:
            
            @SuppressWarnings("unchecked")
            final T protoType = (T)makeInstance(spec);

            instance = protoType;
            break;
            
        default:
            throw new IllegalStateException();
        }

        return instance;
    }
    
    private Object resolveInstance(ClassAwareComponentRequirement requirement) {

        final ClassAwareComponentSpec resolvedSpec = resolveRequirement(requirement);
        
        return instantiate(resolvedSpec);
    }
    
    private Object makeInstance(ClassAwareComponentSpec spec) {

        final Class<?> cl = spec.getType();

        final Object instance;

        try {
            instance = cl.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
        
        // Initialize all requirements
        if (spec.getRequirements() != null) {
            
            for (ClassAwareComponentRequirement requirement : spec.getRequirements()) {
            
                final Object obj = resolveInstance(requirement);
                
                FieldUtil.setFieldValue(instance, requirement.getField(), obj);
            }
        }
        
        return instance;
    }
}
