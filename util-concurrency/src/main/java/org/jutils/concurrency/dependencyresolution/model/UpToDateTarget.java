package org.jutils.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jutils.concurrency.dependencyresolution.executor.Action;
import org.jutils.concurrency.dependencyresolution.executor.ActionWithResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class UpToDateTarget<TARGET> extends TargetDefinition<TARGET> {

    private final UpToDate<?, TARGET> upToDate;
    
    public UpToDateTarget(
            LogContext logContext,
            String semanticType,
            String identifier,
            TargetKey<TARGET> targetKey,
            String semanticAction,
            String description,
            List<Prerequisites> prerequisites,
            UpToDate<?, TARGET> upToDate,
            Action<TARGET> action,
            ActionWithResult<TARGET> actionWithResult) {

        super(
                logContext,
                targetKey,
                prerequisites,
                action,
                actionWithResult,
                new TargetDebug(
                        semanticType,
                        identifier,
                        identifier,
                        semanticAction,
                        description,
                        true));

        Objects.requireNonNull(upToDate);

        this.upToDate = upToDate;
    }

    @Override
    protected <CONTEXT extends TaskContext>
    boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites) {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final UpToDate<CONTEXT, TARGET> call = (UpToDate)upToDate;
        
        return call.isUpToDate(context, target, prerequisites);
    }

    @Override
    protected String getTypeString() {
        return "uptodate";
    }

    @Override
    public String toString() {
        return "UpToDateTarget [debugString=" + getDebugString() + "]";
    }
}
