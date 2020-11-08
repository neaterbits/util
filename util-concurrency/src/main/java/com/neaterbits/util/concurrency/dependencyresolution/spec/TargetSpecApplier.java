package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    static void resolveUnknownTargets(
            LogContext logContext,
            List<TargetDefinition<?>> toResolveList) {
        
        final Map<Object, TargetDefinition<?>> map = new HashMap<>();

        for (TargetDefinition<?> toResolve : toResolveList) {
            mapTargets(toResolve, map);
        }
        
        for (TargetDefinition<?> toResolve : toResolveList) {
            resolveUnknownTargets(logContext, toResolve, map);
        }
    }

    private static <TARGET>
    void mapTargets(TargetDefinition<?> target, Map<Object, TargetDefinition<?>> map) {

        if (!(target instanceof UnknownTarget<?>)) {
            map.put(target.getTargetObject(), target);
        }

        if (target.getPrerequisites() != null) {
                
            for (Prerequisites prerequisites : target.getPrerequisites()) {
             
                for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {

                    if (prerequisite.getSubTarget() != null) {
                        mapTargets(prerequisite.getSubTarget(), map);
                    }
                }
            }
        }
    }

    private static <TARGET> void resolveUnknownTargets(
            LogContext logContext,
            TargetDefinition<TARGET> toResolve,
            Map<Object, TargetDefinition<?>> map) {
        
        if (toResolve instanceof UnknownTarget<?>) {
            throw new IllegalArgumentException();
        }

        if (toResolve.getPrerequisites() != null) {
            
            final List<Prerequisites> updated = new ArrayList<>(toResolve.getPrerequisites().size());
            
            for (Prerequisites prerequisites : toResolve.getPrerequisites()) {
                
                final List<Prerequisite<?>> list = new ArrayList<>(prerequisites.getPrerequisites().size());
                    
                for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {

                    final Prerequisite<?> toAdd;

                    if (prerequisite.getSubTarget() != null) {
                    
                        if (prerequisite.getSubTarget() instanceof UnknownTarget<?>) {
                            
                            final TargetDefinition<?> otherTarget = map.get(prerequisite.getItem());

                            if (otherTarget == null) {
                                throw new IllegalStateException("No target previously made for '"
                                            + prerequisite.getDebugString() + "' in '"
                                            + prerequisite.getDescription() + "'");
                            }

                            @SuppressWarnings({ "unchecked", "rawtypes" })
                            final Prerequisite<?> created = new Prerequisite<Object>(
                                    logContext,
                                    (Prerequisite)prerequisite,
                                    (TargetDefinition)otherTarget);

                            toAdd = created;
                        }
                        else {
                            
                            resolveUnknownTargets(logContext, prerequisite.getSubTarget(), map);
                            
                            toAdd = prerequisite;
                        }
                    }
                    else {
                        toAdd = prerequisite;
                    }
                    
                    list.add(toAdd);
                }
                
                updated.add(new Prerequisites(logContext, prerequisites, list));
            }
            
            toResolve.updatePrerequisites(updated);
        }
    }
}
