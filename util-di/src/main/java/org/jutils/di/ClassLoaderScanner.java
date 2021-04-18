package org.jutils.di;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

import org.jutils.di.componentsource.ComponentsSourceException;
import org.jutils.di.componentsource.ComponentsSourceLoader;
import org.jutils.di.componentsource.classloader.ClassLoaderComponentsSource;

public class ClassLoaderScanner extends DelegatingScanner {

    public Resolver scan(
            URLClassLoader classLoader,
            List<ComponentsSourceLoader<?>> loaders) throws IOException, ComponentsSourceException, ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        final ClassLoaderComponentsSource classLoaderSource = new ClassLoaderComponentsSource(classLoader, loaders);
        
        return scan(classLoaderSource, classLoader);
    }
}
