package com.neaterbits.util.di;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.jarfile.JarComponentsSource;

public class JarScanner extends DelegatingScanner {

    public Resolver scan(List<URL> jarFiles, List<ComponentsSourceLoader<?>> loaders) throws IOException, ComponentsSourceException, ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        final JarComponentsSource jarSource = new JarComponentsSource(jarFiles, loaders);
        
        return scan(jarSource, URLClassLoader.newInstance(jarFiles.toArray(new URL[jarFiles.size()])));
    }
}
