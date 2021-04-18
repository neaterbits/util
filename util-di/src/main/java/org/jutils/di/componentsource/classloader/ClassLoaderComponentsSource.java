package org.jutils.di.componentsource.classloader;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

import org.jutils.di.componentsource.ComponentsSourceException;
import org.jutils.di.componentsource.ComponentsSourceLoader;
import org.jutils.di.componentsource.DelegatingComponentSource;

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
