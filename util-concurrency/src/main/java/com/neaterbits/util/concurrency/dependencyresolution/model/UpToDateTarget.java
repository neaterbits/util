package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;

public final class UpToDateTarget<TARGET> extends TargetDefinition<TARGET> {

    private final UpToDate<TARGET> upToDate;
    
    public UpToDateTarget(
            LogContext logContext,
            String identifier,
            TargetKey<TARGET> targetKey,
            String description,
            List<Prerequisites> prerequisites,
            UpToDate<TARGET> upToDate,
            Action<TARGET> action,
            ActionWithResult<TARGET> actionWithResult) {

        super(logContext, identifier, identifier, targetKey, description, prerequisites, action, actionWithResult);

        Objects.requireNonNull(upToDate);

        this.upToDate = upToDate;
    }

    @Override
    protected boolean isUpToDate(TARGET target, Collection<Prerequisites> prerequisites) {

        return upToDate.isUpToDate(target, prerequisites);
    }

    @Override
    public String getDebugString() {

        return getTargetObject().toString();
    }
}
