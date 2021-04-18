package org.jutils.di;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jutils.di.componentsource.ComponentsSourceException;
import org.jutils.di.componentsource.ComponentsSourceLoader;
import org.jutils.di.componentsource.jarfile.JarComponentsSource;

public class JarScanner extends DelegatingScanner {

    public Resolver scan(List<URL> jarFiles, List<ComponentsSourceLoader<?>> loaders) throws IOException, ComponentsSourceException, ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        final JarComponentsSource jarSource = new JarComponentsSource(jarFiles, loaders);
        
        return scan(jarSource, URLClassLoader.newInstance(jarFiles.toArray(new URL[jarFiles.size()])));
    }
}
