package com.neaterbits.util.di.componentsource.classloader;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.DelegatingComponentSource;

public final class ClassLoaderComponentsSource extends DelegatingComponentSource {
    
    private final URLClassLoader classLoader;

    public ClassLoaderComponentsSource(URLClassLoader classLoader, List<ComponentsSourceLoader<?>> scanLoaders) {
        super(scanLoaders);

        Objects.requireNonNull(classLoader);
        
        this.classLoader = classLoader;
    }

    @Override
    public void scanForComponentSpecs(ComponentSpecProcessor<Object> processor)
            throws IOException, ComponentsSourceException {

        super.scanForComponentSpecs(classLoader, processor);
    }
}
