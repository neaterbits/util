package com.neaterbits.util.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class ComponentSpecsTest {

    interface Role {
        
    }

    interface OtherRole {
        
    }

    static class Type1 implements Role {

    }

    static class Type1WithSomeField implements Role {

        private OtherRole someField;
    }

    static class Type2 implements OtherRole {
        
    }

    @Test
    public void testInstantiateWithFieldSingletons() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1WithSomeField.class, Role.class, null, Instantiation.SINGLETON);
        
        addSpec(specs, Type2.class, OtherRole.class, null, Instantiation.SINGLETON);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        final Resolver resolver = new Resolver(specs, getClass().getClassLoader());
        
        final Type1WithSomeField object = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(object).isNotNull();
        assertThat(object.someField).isNotNull();
        
        // Get another time to verify that are singletons

        final Type1WithSomeField sameObject = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(sameObject).isSameAs(object);
        assertThat(sameObject.someField).isSameAs(object.someField);
    }

    @Test
    public void testInstantiateWithFieldSingletonAndPrototype() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1WithSomeField.class, Role.class, null, Instantiation.SINGLETON);
        
        addSpec(specs, Type2.class, OtherRole.class, null, Instantiation.PROTOTYPE);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        final Resolver resolver = new Resolver(specs, getClass().getClassLoader());
        
        final Type1WithSomeField object = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(object).isNotNull();
        assertThat(object.someField).isNotNull();
        
        // Get another time to verify that are singletons

        final Type1WithSomeField otherObject = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(otherObject).isSameAs(object);
        assertThat(otherObject.someField).isSameAs(object.someField);
    }

    @Test
    public void testInstantiateWithPrototypes() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1WithSomeField.class, Role.class, null, Instantiation.PROTOTYPE);
        
        addSpec(specs, Type2.class, OtherRole.class, null, Instantiation.PROTOTYPE);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        final Resolver resolver = new Resolver(specs, getClass().getClassLoader());
        
        final Type1WithSomeField object = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(object).isNotNull();
        assertThat(object.someField).isNotNull();
        
        // Get another time to verify that are singletons

        final Type1WithSomeField sameObject = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(sameObject).isNotSameAs(object);
        assertThat(sameObject.someField).isNotSameAs(object.someField);
    }

    @Test
    public void testInstantiateWithPrototypesAndSingleton() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1WithSomeField.class, Role.class, null, Instantiation.PROTOTYPE);
        
        addSpec(specs, Type2.class, OtherRole.class, null, Instantiation.SINGLETON);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        final Resolver resolver = new Resolver(specs, getClass().getClassLoader());
        
        final Type1WithSomeField object = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(object).isNotNull();
        assertThat(object.someField).isInstanceOf(Type2.class);
        assertThat(object.someField).isNotNull();
        
        // Get another time to verify that are singletons

        final Type1WithSomeField otherObject = resolver.instantiate(Type1WithSomeField.class);
        
        assertThat(otherObject).isNotSameAs(object);
        assertThat(otherObject.someField).isInstanceOf(Type2.class);
        assertThat(otherObject.someField).isSameAs(object.someField);
    }

    @Test
    public void testNoComponentsForRole() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1WithSomeField.class, Role.class, null, Instantiation.SINGLETON);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        try {
            new Resolver(specs, getClass().getClassLoader());

            fail("Expected exception");
        } catch (UnresolvedRequirementException ex) {
        }
    }

    @Test
    public void testNoFieldForRequirement() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        final CollectedComponentSpec spec = addSpec(specs, Type1.class, Role.class, null, Instantiation.SINGLETON);
        
        addSpec(specs, Type2.class, OtherRole.class, null, Instantiation.SINGLETON);
        
        addRequirement(spec, OtherRole.class, null, "someField");
        
        try {
            new Resolver(specs, getClass().getClassLoader());

            fail("Expected exception");
        } catch (UnknownFieldException ex) {
        }
    }

    @Test
    public void testAmbiguousComponents() throws ClassNotFoundException, ResolveException {
        
        final CollectedComponentSpecs specs = new CollectedComponentSpecs();

        addSpec(specs, Type1.class, Role.class, null, Instantiation.SINGLETON);
        addSpec(specs, Type2.class, Role.class, null, Instantiation.SINGLETON);
        
        try {
            new Resolver(specs, getClass().getClassLoader());
        } catch (AmbiguousRequirementException ex) {
            fail("Expected exception");
        }
    }
    
    private static CollectedComponentSpec addSpec(
            CollectedComponentSpecs specs,
            Class<?> type,
            Class<?> role,
            Object roleHint,
            Instantiation instantiation) {
        
        return specs.addSpec(type.getName(), role.getName(), roleHint, instantiation);
    }
    
    private static void addRequirement(
            CollectedComponentSpec spec,
            Class<?> role,
            Object roleHint,
            String fieldName) {
        
        spec.addRequirement(role.getName(), roleHint, fieldName);
    }
}
