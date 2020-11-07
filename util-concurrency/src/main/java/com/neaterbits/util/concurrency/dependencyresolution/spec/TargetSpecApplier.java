package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.Collection;
import java.util.List;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

abstract class TargetSpecApplier {

    static class Config<CONTEXT> {
        
        final LogContext logContext;
        final CONTEXT context;
        final TargetFinderLogger logger;
        final int indent;
        
        Config(LogContext logContext, CONTEXT context, TargetFinderLogger logger) {
            this(logContext, context, logger, 0);
        }
        
        private Config(LogContext logContext, CONTEXT context, TargetFinderLogger logger, int indent) {
            this.logContext = logContext;
            this.context = context;
            this.logger = logger;
            this.indent = indent;
        }
        
        
        Config<CONTEXT> addIndent() {
            return new Config<>(logContext, context, logger, indent + 1);
        }
    }

    static <CONTEXT extends TaskContext, TARGET>
    TargetDefinition<TARGET> makeTarget(
            Config<CONTEXT> config,
            TargetSpec<CONTEXT, TARGET> targetSpec,
            TARGET target,
            List<Prerequisites> prerequisites) {

        final TargetDefinition<TARGET> createdTargetDefinition;
        
        if (
               (prerequisites == null || prerequisites.isEmpty())
            && !targetSpec.hasAction()) {
            
            // Link to target specified elsewhere
            throw new UnsupportedOperationException();
        }
        else {
        
            final TargetDefinition<TARGET> createdTarget = targetSpec.createTargetDefinition(
                    config.logContext,
                    config.context,
                    target,
                    prerequisites);
            
            if (config.logger != null) {
                config.logger.onFoundPrerequisites(config.indent, createdTarget, prerequisites);
            }
            
            createdTargetDefinition = createdTarget;
        }
        
        return createdTargetDefinition;
    }
    
    static <CONTEXT extends TaskContext, TARGET, PREREQUISITE>
    Prerequisites makePrerequisites(
            LogContext logContext,
            Collection<Prerequisite<?>> prerequisitesList,
            PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec) {
        
        final Prerequisites prerequisites = new Prerequisites(
                logContext,
                prerequisitesList,
                prerequisiteSpec.getDescription(),
                prerequisiteSpec.getRecursiveBuildInfo(),
                prerequisiteSpec.getCollectors());

        return prerequisites;
    }
}
