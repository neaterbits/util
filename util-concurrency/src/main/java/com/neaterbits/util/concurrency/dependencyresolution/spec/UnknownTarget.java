package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.Collection;
import java.util.Collections;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDebug;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetKey;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class UnknownTarget<TARGET> extends TargetDefinition<TARGET> {

    @SuppressWarnings("unchecked")
    public UnknownTarget(LogContext logContext, TARGET targetObject) {
        super(
                logContext,
                new TargetKey<>((Class<TARGET>)targetObject.getClass(), targetObject),
                Collections.emptyList(),
                new Action<>(null, (context, target, params) -> null),
                null,
                new TargetDebug("unknown", null, null, null, null, false));
    }

    @Override
    protected <CONTEXT extends TaskContext>
    boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites) {
        return false;
    }

    @Override
    public String getDebugString() {
        return getTargetObject().toString();
    }
}
