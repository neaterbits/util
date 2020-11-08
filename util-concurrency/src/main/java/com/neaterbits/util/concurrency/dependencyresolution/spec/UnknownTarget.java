package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.Collections;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetKey;

final class UnknownTarget<TARGET> extends TargetDefinition<TARGET> {

    @SuppressWarnings("unchecked")
    public UnknownTarget(LogContext logContext, TARGET targetObject) {
        super(
                logContext, null, null,
                new TargetKey<>((Class<TARGET>)targetObject.getClass(), targetObject),
                null,
                Collections.emptyList(),
                new Action<>(null, (context, target, params) -> null),
                null);
    }

    @Override
    public String getLogIdentifier() {
        return null;
    }

    @Override
    public String getLogLocalIdentifier() {
        return null;
    }

    @Override
    public String targetSimpleLogString() {
        return null;
    }

    @Override
    public String targetToLogString() {
        return null;
    }

    @Override
    public String getDebugString() {
        return getTargetObject().toString();
    }
}
