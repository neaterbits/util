package com.neaterbits.util.di.componentsource;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

public abstract class DelegatingComponentSource implements ComponentsSource<Object> {

    private final List<ComponentsSourceLoader<?>> scanLoaders;

    protected DelegatingComponentSource(List<ComponentsSourceLoader<?>> scanLoaders) {

        Objects.requireNonNull(scanLoaders);
        
        this.scanLoaders = scanLoaders;
    }

    protected final void scanForComponentSpecs(URLClassLoader classLoader, ComponentSpecProcessor<Object> processor)
                        throws IOException, ComponentsSourceException {
        
        for (ComponentsSourceLoader<?> sourceLoader : scanLoaders) {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            final List<ComponentsSource<Object>> loaded = (List<ComponentsSource<Object>>)(List) sourceLoader.load(classLoader);
            
            for (ComponentsSource<Object> source : loaded) {
                source.scanForComponentSpecs((componentSpec, reqSource, type, role, roleHint, instantiation)
                                    -> processor.onComponentSpec(componentSpec, reqSource, type, role, roleHint, instantiation));
            }
        }
    }
    
    @Override
    public final void scanForRequirements(Object componentSpec, RequirementProcessor processor) {
        
        throw new UnsupportedOperationException("Should call source from processor");
    }
}
