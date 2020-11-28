package com.neaterbits.util.di.componentsource.jsr330;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.neaterbits.util.di.componentsource.BaseComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.ComponentsSource;
import com.neaterbits.util.di.componentsource.ComponentsSourceException;

public final class JSR330ClassComponentsSourceLoader
        extends BaseComponentsSourceLoader<
                            Class<?>,
                            List<Class<?>>,
                            JSR330ClassComponentsSource> {

    @Override
    public List<? extends ComponentsSource<Class<?>>> load(URLClassLoader classLoader)
            throws IOException, ComponentsSourceException {

        
        final List<JSR330ClassComponentsSource> sources = processAll(
                classLoader,
                "META-INF/sisu/javax.inject.Named",
                (url, inputStream) -> readComponentSet(classLoader, url, inputStream),
                JSR330ClassComponentsSource::new);
        
        return sources;
    }
    
    private static List<Class<?>> readComponentSet(URLClassLoader classLoader, URL url, InputStream inputStream) throws IOException, ComponentsSourceException {
        
        final List<Class<?>> classes = new ArrayList<>();
        
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line;
        
        while (null != (line = reader.readLine())) {
            
            final Class<?> cl;
            
            try {
                cl = classLoader.loadClass(line);
            } catch (ClassNotFoundException ex) {
                throw new ComponentsSourceException("Could not load class '" + line + "'", ex);
            }
        
            classes.add(cl);
        }

        return classes;
    }
}