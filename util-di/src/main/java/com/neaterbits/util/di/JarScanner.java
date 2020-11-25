package com.neaterbits.util.di;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.neaterbits.util.di.componentsource.ComponentsSourceException;
import com.neaterbits.util.di.componentsource.ComponentsSourceLoader;
import com.neaterbits.util.di.componentsource.jarfile.JarComponentsSource;

public class JarScanner {

    public static void scan(
            List<URL> jarFiles,
            List<ComponentsSourceLoader<?>> loaders,
            CollectedComponentSpecs componentSpecs) throws IOException, ComponentsSourceException {
        
        final JarComponentsSource jarSource = new JarComponentsSource(jarFiles, loaders);
        
        jarSource.scanForComponentSpecs((spec, reqSource, type, role, roleHint, instantiation) -> {

            final CollectedComponentSpec componentSpec = componentSpecs.addSpec(type, role, roleHint, instantiation);
            
            reqSource.scanForRequirements(spec, (reqRole, reqRoleHint, reqFieldName) -> {
                
                componentSpec.addRequirement(reqRole, roleHint, reqFieldName);
            });
        });
    }
}
