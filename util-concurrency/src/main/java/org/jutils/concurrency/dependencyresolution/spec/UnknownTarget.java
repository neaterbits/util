package org.jutils.concurrency.dependencyresolution.spec;

import java.util.Collection;
import java.util.Collections;

import org.jutils.concurrency.dependencyresolution.executor.Action;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDebug;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.model.TargetKey;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

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
    protected String getTypeString() {
        return "unknown";
    }
}
