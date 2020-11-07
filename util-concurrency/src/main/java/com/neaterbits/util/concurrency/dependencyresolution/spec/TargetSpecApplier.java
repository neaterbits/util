package com.neaterbits.util.concurrency.dependencyresolution.spec;

import com.neaterbits.structuredlog.binary.logging.LogContext;

abstract class TargetSpecApplier {

    static class Config<CONTEXT> {
        
        final LogContext logContext;
        final CONTEXT context;
        final TargetFinderLogger logger;
        
        Config(LogContext logContext, CONTEXT context, TargetFinderLogger logger) {
            this.logContext = logContext;
            this.context = context;
            this.logger = logger;
        }
    }
}
