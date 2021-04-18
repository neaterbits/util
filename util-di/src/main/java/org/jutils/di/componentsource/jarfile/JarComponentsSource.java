package org.jutils.di.componentsource.jarfile;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jutils.di.componentsource.ComponentsSource;
import org.jutils.di.componentsource.ComponentsSourceException;
import org.jutils.di.componentsource.ComponentsSourceLoader;
import org.jutils.di.componentsource.DelegatingComponentSource;

public class JarComponentsSource
        extends DelegatingComponentSource
        implements ComponentsSource<Object> {

    private final List<URL> jarFiles;
    
    public JarComponentsSource(List<URL> jarFiles, List<ComponentsSourceLoader<?>> scanLoaders) {
    
        super(scanLoaders);
        
        this.jarFiles = jarFiles;
    }

    @Override
    public void scanForComponentSpecs(ComponentSpecProcessor<Object> processor) throws IOException, ComponentsSourceException {
        
        for (URL url : jarFiles) {
            try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL [] { url })) {
                scanForComponentSpecs(classLoader, processor);
            }
        }
    }
}
