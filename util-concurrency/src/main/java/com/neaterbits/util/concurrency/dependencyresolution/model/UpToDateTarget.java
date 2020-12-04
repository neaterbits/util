package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class UpToDateTarget<TARGET> extends TargetDefinition<TARGET> {

    private final UpToDate<?, TARGET> upToDate;
    
    public UpToDateTarget(
            LogContext logContext,
            String identifier,
            TargetKey<TARGET> targetKey,
            String description,
            List<Prerequisites> prerequisites,
            UpToDate<?, TARGET> upToDate,
            Action<TARGET> action,
            ActionWithResult<TARGET> actionWithResult) {

        super(logContext, identifier, identifier, targetKey, description, prerequisites, action, actionWithResult);

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
    public String getDebugString() {

        return getTargetObject().toString();
    }

    @Override
    public String toString() {
        return "UpToDateTarget [debugString=" + getDebugString() + "]";
    }
}
