package org.jutils.di;

import java.io.IOException;

import org.jutils.di.componentsource.ComponentsSourceException;
import org.jutils.di.componentsource.DelegatingComponentSource;

abstract class DelegatingScanner {

    final Resolver scan(DelegatingComponentSource componentSource, ClassLoader classLoader)
                throws IOException, ComponentsSourceException, ClassNotFoundException, UnsupportedTypeException, UnknownFieldException, UnresolvedRequirementException, AmbiguousRequirementException {
        
        final CollectedComponentSpecs componentSpecs = new CollectedComponentSpecs();
        
        componentSource.scanForComponentSpecs((spec, reqSource, type, role, roleHint, instantiation) -> {

            final CollectedComponentSpec componentSpec = componentSpecs.addSpec(
                                                                    reqSource.getSource(),
                                                                    type,
                                                                    role,
                                                                    roleHint,
                                                                    instantiation);
            
            reqSource.scanForRequirements(spec, (reqRole, reqRoleHint, reqFieldName) -> {
                
                componentSpec.addRequirement(reqRole, roleHint, reqFieldName);
            });
        });
        
        return new Resolver(componentSpecs, classLoader);
    }
}
