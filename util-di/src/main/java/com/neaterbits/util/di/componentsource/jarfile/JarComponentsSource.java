package com.neaterbits.util.di.componentsource.jarfile;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.neaterbits.util.di.componentsource.ComponentsSource;
import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.DelegatingComponentSource;

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
