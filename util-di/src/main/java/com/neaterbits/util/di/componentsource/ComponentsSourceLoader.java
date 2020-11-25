package com.neaterbits.util.di.componentsource;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;

public interface ComponentsSourceLoader<COMPONENT_SPEC> {

    List<? extends ComponentsSource<COMPONENT_SPEC>> load(URLClassLoader classLoader) throws IOException, ComponentsSourceException;

}
