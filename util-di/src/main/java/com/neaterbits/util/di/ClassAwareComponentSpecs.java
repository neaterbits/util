package com.neaterbits.util.di;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import com.neaterbits.util.reflection.FieldUtil;

@Immutable
final class ClassAwareComponentSpecs {

    private final List<ClassAwareComponentSpec> specs;
    
    private final Map<Class<?>, ClassAwareComponentSpec> byType;
    
    private final Map<Class<?>, List<ClassAwareComponentSpec>> byRole;

    ClassAwareComponentSpecs(
            CollectedComponentSpecs collectedComponentSpecs,
            ClassLoader classLoader)
                    throws ClassNotFoundException,
                           UnsupportedTypeException,
                           UnknownFieldException,
                           UnresolvedRequirementException,
                           AmbiguousRequirementException {

        final List<CollectedComponentSpec> all = collectedComponentSpecs.getAll();
        
        final List<ClassAwareComponentSpec> classAwareSpecs = new ArrayList<>(all.size());
        
        for (CollectedComponentSpec collectedSpec : all) {
            
            classAwareSpecs.add(makeClassAware(collectedSpec, classLoader));
        }
        
        this.specs = Collections.unmodifiableList(classAwareSpecs);
        
        
        this.byType = new HashMap<>();
        
        for (ClassAwareComponentSpec spec : specs) {
            
            final ClassAwareComponentSpec existing = byType.put(spec.getType(), spec);
            
            if (existing != null) {
                throw new IllegalArgumentException("Multiple components of type '"
                                    + spec.getType().getName() + "'");
            }
        }
        
        final Map<Class<?>, List<ClassAwareComponentSpec>> map = new HashMap<>();
        
        for (ClassAwareComponentSpec spec : specs) {

            for (Class<?> role : spec.getRoles()) {

                List<ClassAwareComponentSpec> roleSpecs = map.get(role);
                
                if (roleSpecs == null) {
                    roleSpecs = new ArrayList<>();
                    
                    map.put(role, roleSpecs);
                }
                
                roleSpecs.add(spec);
            }
        }
    
        // Make map immutable
        this.byRole = map.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> Collections.unmodifiableList(entry.getValue())));
        
        // Verify that all requirements resolve to one type
        verifyRequirementsCanBeResolved(specs, byRole);
    }
    
    private static ClassAwareComponentSpec makeClassAware(CollectedComponentSpec spec, ClassLoader classLoader)
                        throws ClassNotFoundException,
                        UnsupportedTypeException,
                        UnknownFieldException {
        
        final Class<?> cl = classLoader.loadClass(spec.getType());
        
        verifyType(cl);

        final List<Class<?>> roles;
        
        if (spec.getRole() != null) {
            // Role specified
            roles = Arrays.asList(classLoader.loadClass(spec.getRole()));
        }
        else {
            // Any interface implemented by type, or type itself
            final Set<Class<?>> distinctTypes = new HashSet<>();
            
            for (Class<?> intf : cl.getInterfaces()) {
                
                distinctTypes.add(intf);
                
                getAllExtendedInterfaces(cl, distinctTypes);
            }
            
            for (Class<?> c = cl; c.getSuperclass() != null; c = c.getSuperclass()) {
                distinctTypes.add(c);
            }
            
            roles = new ArrayList<>(distinctTypes);
        }
        
        return new ClassAwareComponentSpec(
                            cl,
                            Collections.unmodifiableList(roles),
                            spec.getRoleHint(),
                            spec.getInstantiation(),
                            spec.getRequirements() != null
                                ? convertRequirements(cl, spec.getRequirements(), classLoader)
                                : null);
    }
    
    private static List<ClassAwareComponentRequirement> convertRequirements(
                                                                    Class<?> componentCl,
                                                                    List<CollectedComponentRequirement> collectedRequirements,
                                                                    ClassLoader classLoader) throws ClassNotFoundException, UnknownFieldException {
        
        final List<ClassAwareComponentRequirement> list = new ArrayList<>(collectedRequirements.size());
        
        for (CollectedComponentRequirement collected : collectedRequirements) {
            
            final Class<?> role = classLoader.loadClass(collected.getRole());
            
            final Field field = FieldUtil.getFieldInClassOrBaseClass(componentCl, collected.getFieldName());
            
            if (field == null) {
                throw new UnknownFieldException("No accessible field '" + collected.getFieldName()
                                                    + "' in class '" + componentCl.getName() + "'");
            }
            
            final ClassAwareComponentRequirement requirement
                = new ClassAwareComponentRequirement(
                                        role,
                                        collected.getRoleHint(),
                                        field);
            
            list.add(requirement);
        }
        
        return list;
    }
    
    private static void verifyType(Class<?> cl) throws UnsupportedTypeException {

        if (cl.isInterface()) {
            throw new UnsupportedTypeException("Type " + cl.getName() + " is interface");
        }

        if (cl.isAnnotation()) {
            throw new UnsupportedTypeException("Type " + cl.getName() + " is annotation");
        }

        if (cl.isAnonymousClass()) {
            throw new UnsupportedTypeException("Type " + cl.getName() + " is anonymous class");
        }

        if (cl.isArray()) {
            throw new UnsupportedTypeException("Type " + cl.getName() + " is array class");
        }
        
        if (cl.isSynthetic()) {
            throw new UnsupportedTypeException("Type " + cl.getName() + " is syntetic class");
        }
    }
    
    private static void verifyRequirementsCanBeResolved(
            List<ClassAwareComponentSpec> specs,
            Map<Class<?>, List<ClassAwareComponentSpec>> byRole) throws UnresolvedRequirementException, AmbiguousRequirementException {
        
        for (ClassAwareComponentSpec spec : specs) {

            for (ClassAwareComponentRequirement requirement : spec.getRequirements()) {

                final ClassAwareComponentSpec resolved = resolve(requirement, byRole::get);
                
                if (resolved == null) {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    ClassAwareComponentSpec getByType(Class<?> type) {
        
        Objects.requireNonNull(type);
        
        return byType.get(type);
    }

    ClassAwareComponentSpec resolve(ClassAwareComponentRequirement requirement) {
        return resolve(requirement.getRole(), requirement.getRoleHint());
    }

    ClassAwareComponentSpec resolve(Class<?> role, Object roleHint) {
    
        try {
            return resolve(role, roleHint, byRole::get);
        } catch (UnresolvedRequirementException | AmbiguousRequirementException ex) {
            throw new IllegalStateException("Should already have been resolved", ex);
        }
    }

    private static ClassAwareComponentSpec resolve(
            ClassAwareComponentRequirement requirement,
            Function<Class<?>, List<ClassAwareComponentSpec>> byRole)
                    throws UnresolvedRequirementException, AmbiguousRequirementException {
        
        return resolve(requirement.getRole(), requirement.getRoleHint(), byRole);
    }
        
    private static ClassAwareComponentSpec resolve(
            Class<?> role,
            Object roleHint,
            Function<Class<?>, List<ClassAwareComponentSpec>> byRole)
                    throws UnresolvedRequirementException, AmbiguousRequirementException {

        final List<ClassAwareComponentSpec> roleSpecs = byRole.apply(role);

        final ClassAwareComponentSpec found;
        
        if (roleSpecs == null || roleSpecs.isEmpty()) {
            throw new UnresolvedRequirementException("No components matching role type '"
                                    + role.getName() + "'");
        }
        else {

            final List<ClassAwareComponentSpec> matching = new ArrayList<>(roleSpecs.size());
            
            for (ClassAwareComponentSpec spec : roleSpecs) {
                
                if (spec.matches(role, roleHint)) {
                    matching.add(spec);
                }
            }

            switch (matching.size()) {
            case 0:
                throw new UnresolvedRequirementException("No components matching role type '"
                        + role.getName() + "'");
                
            case 1:
                found = matching.get(0);
                break;
             
            default:
                
                final StringBuilder sb = new StringBuilder("Multiple matches: ");
                
                for (int i = 0; i < matching.size(); ++ i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    
                    sb.append(matching.get(i).getType().getName());
                }
                
                throw new AmbiguousRequirementException(sb.toString());
            }
        }
        
        return found;
    }
    
    private static void getAllExtendedInterfaces(Class<?> cl, Set<Class<?>> dst) {
        
        if (!cl.isInterface()) {
            throw new IllegalArgumentException();
        }
        
        for (Class<?> extended : cl.getInterfaces()) {
            dst.add(extended);
            
            getAllExtendedInterfaces(extended, dst);
        }
    }
}
