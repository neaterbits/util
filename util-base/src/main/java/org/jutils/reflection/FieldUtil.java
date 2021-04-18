package org.jutils.reflection;

import java.lang.reflect.Field;

public class FieldUtil {

    public static Field getFieldInClassOrBaseClass(Class<?> fieldClass, String fieldName) {

        Field found = null;
        
        for (Class<?> cl = fieldClass; cl != null; cl = cl.getSuperclass()) {
            
            try {
                found = cl.getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
            
            if (found != null) {
                break;
            }
        }
        
        return found;
    }
    
    public static void setFieldValue(Object object, Field field, Object value) {
        
        final boolean fieldAccessible = field.canAccess(object);

        if (!fieldAccessible) {
            field.setAccessible(true);
        }

        try {
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        finally {
            if (!fieldAccessible) {
                field.setAccessible(false);
            }
        }
    }
}
