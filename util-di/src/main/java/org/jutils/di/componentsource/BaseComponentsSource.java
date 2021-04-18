package org.jutils.di.componentsource;

import java.net.URL;

public abstract class BaseComponentsSource<COMPONENT_SPEC> implements ComponentsSource<COMPONENT_SPEC> {

    private final URL source;
    
    protected BaseComponentsSource(URL source) {
        this.source = source;
    }

    @Override
    public final URL getSource() {
        return source;
    }
}
