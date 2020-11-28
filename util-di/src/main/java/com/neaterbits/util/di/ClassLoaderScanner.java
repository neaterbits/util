package com.neaterbits.util.di;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.classloader.ClassLoaderComponentsSource;

public class ClassLoaderScanner extends DelegatingScanner {

    public Resolver scan(
            URLClassLoader classLoader,
            List<ComponentsSourceLoader<?>> loaders) throws IOException, ComponentsSourceException, ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        final ClassLoaderComponentsSource classLoaderSource = new ClassLoaderComponentsSource(classLoader, loaders);
        
        return scan(classLoaderSource, classLoader);
    }
}
