package org.jutils.di.componentsource.jsr330;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jutils.di.Instantiation;
import org.jutils.di.componentsource.ComponentsSource;

final class JSR330ClassComponentsSource implements ComponentsSource<Class<?>> {

    private final Collection<Class<?>> classes;
    private final URL source;
    
    public JSR330ClassComponentsSource(Collection<Class<?>> classes, URL source) {
        
        Objects.requireNonNull(classes);
        
        this.classes = classes;
        this.source = source;
    }

    @Override
    public URL getSource() {
        return source;
    }

    @Override
    public void scanForComponentSpecs(ComponentSpecProcessor<Class<?>> processor) {

        for (Class<?> componentClass : classes) {

            final String type = componentClass.getName();
            
            processor.onComponentSpec(
                    componentClass,
                    this,
                    type,
                    null,
                    findRoleHint(componentClass.getAnnotations()),
                    Instantiation.PROTOTYPE);
        }
    }

    @Override
    public void scanForRequirements(Class<?> componentClass, RequirementProcessor processor) {
    
        for (Class<?> cl = componentClass; !cl.equals(Object.class); cl = cl.getSuperclass()) {

            for (Field field : cl.getDeclaredFields()) {

                final Inject inject = field.getAnnotation(Inject.class);
                
                if (inject != null) {
                    processor.onRequirement(
                            field.getType().getName(),
                            findRoleHint(field.getAnnotations()),
                            field.getName());
                }
            }
        }
    }
    
    private static Object findRoleHint(Annotation [] annotations) {
        
        Object roleHint = null;
        
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                roleHint = annotation;
                break;
            }
        }

        return roleHint;
    }
}
