package com.neaterbits.util.di.componentsource.jarfile;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.neaterbits.util.di.componentsource.ComponentsSource;
import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;

public class JarComponentsSource implements ComponentsSource<Object> {

    private final List<URL> jarFiles;
    private final List<ComponentsSourceLoader<?>> scanLoaders;
    
    public JarComponentsSource(List<URL> jarFiles, List<ComponentsSourceLoader<?>> scanLoaders) {
        this.jarFiles = jarFiles;
        this.scanLoaders = scanLoaders;
    }

    @Override
    public void scanForComponentSpecs(ComponentSpecProcessor<Object> processor) throws IOException, ComponentsSourceException {
        
        for (URL url : jarFiles) {
            try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL [] { url })) {

                for (ComponentsSourceLoader<?> sourceLoader : scanLoaders) {

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    final List<ComponentsSource<Object>> loaded = (List<ComponentsSource<Object>>)(List) sourceLoader.load(classLoader);
                    
                    for (ComponentsSource<Object> source : loaded) {
                        source.scanForComponentSpecs((componentSpec, reqSource, type, role, roleHint, instantiation)
                                            -> processor.onComponentSpec(componentSpec, reqSource, type, role, roleHint, instantiation));
                    }
                }
            }
        }
    }

    @Override
    public void scanForRequirements(Object componentSpec, RequirementProcessor processor) {
        
        throw new UnsupportedOperationException("Should call source from processor");
    }
}
