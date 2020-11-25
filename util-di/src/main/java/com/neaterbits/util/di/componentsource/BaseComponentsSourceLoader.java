package com.neaterbits.util.di.componentsource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseComponentsSourceLoader<
            COMPONENT_SPEC,
            COMPONENT_SET_SPEC,
            COMPONENTS_SOURCE extends ComponentsSource<COMPONENT_SPEC>>
    implements ComponentsSourceLoader<COMPONENT_SPEC> {

    @FunctionalInterface
    protected interface ReadSpec<COMPONENTS_SPEC> {
        
        COMPONENTS_SPEC read(URL url, InputStream inputStream) throws IOException, ComponentsSourceException;
    }
        
    protected final List<COMPONENTS_SOURCE>
    processAll(
            URLClassLoader classLoader,
            String name,
            ReadSpec<COMPONENT_SET_SPEC> readSpec,
            Function<COMPONENT_SET_SPEC, COMPONENTS_SOURCE> createSource
            ) throws IOException, ComponentsSourceException {

        final Enumeration<URL> resources = classLoader.findResources(name);
        
        final List<COMPONENT_SET_SPEC> componentSpecs = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            
            final URL url = resources.nextElement();

            try (InputStream inputStream = url.openStream()) {
                
                final COMPONENT_SET_SPEC spec = readSpec.read(url, inputStream);

                componentSpecs.add(spec);
            }
        }
        
        return componentSpecs.stream()
                .map(createSource)
                .collect(Collectors.toList());
        
    }
}
