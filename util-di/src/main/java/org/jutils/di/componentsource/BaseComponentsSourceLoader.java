package org.jutils.di.componentsource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class BaseComponentsSourceLoader<
            COMPONENT_SPEC,
            COMPONENT_SET_SPEC,
            COMPONENTS_SOURCE extends ComponentsSource<COMPONENT_SPEC>>
    implements ComponentsSourceLoader<COMPONENT_SPEC> {

    @FunctionalInterface
    protected interface ReadSpec<COMPONENTS_SPEC> {
        
        COMPONENTS_SPEC read(URL url, InputStream inputStream) throws IOException, ComponentsSourceException;
    }
    
    @FunctionalInterface
    protected interface CreateSource<
            COMPONENT_SPEC,
            COMPONENT_SET_SPEC,
            COMPONENTS_SOURCE extends ComponentsSource<COMPONENT_SPEC>> {
        
        COMPONENTS_SOURCE create(COMPONENT_SET_SPEC componentSet, URL source);
    }
        
    protected final List<COMPONENTS_SOURCE>
    processAll(
            URLClassLoader classLoader,
            String name,
            ReadSpec<COMPONENT_SET_SPEC> readSpec,
            CreateSource<COMPONENT_SPEC, COMPONENT_SET_SPEC, COMPONENTS_SOURCE> createSource
            ) throws IOException, ComponentsSourceException {

        final Enumeration<URL> resources = classLoader.findResources(name);
        
        final List<COMPONENTS_SOURCE> sources = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            
            final URL url = resources.nextElement();

            try (InputStream inputStream = url.openStream()) {
                
                final COMPONENT_SET_SPEC spec = readSpec.read(url, inputStream);

                final COMPONENTS_SOURCE source = createSource.create(spec, url);
                
                if (source == null) {
                    throw new IllegalStateException();
                }

                sources.add(source);
            }
        }
        
        return sources;
        
    }
}
