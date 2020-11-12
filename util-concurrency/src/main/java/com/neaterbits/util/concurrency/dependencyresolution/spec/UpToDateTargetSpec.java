package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetKey;
import com.neaterbits.util.concurrency.dependencyresolution.model.UpToDate;
import com.neaterbits.util.concurrency.dependencyresolution.model.UpToDateTarget;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionFunction;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class UpToDateTargetSpec<CONTEXT extends TaskContext, TARGET>
    extends TargetSpec<CONTEXT, TARGET> {

    private final UpToDate<TARGET> upToDate;
    private final Function<TARGET, String> getIdentifier;
    
    public UpToDateTargetSpec(
            Class<TARGET> type,
            UpToDate<TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription,
            List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites, Constraint constraint,
            ActionFunction<CONTEXT, TARGET> actionFunction,
            ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult,
            ProcessResult<CONTEXT, TARGET, ?> onResult) {
        super(type, getDescription, prerequisites, constraint, actionFunction, actionWithResult, onResult);

        Objects.requireNonNull(upToDate);
        Objects.requireNonNull(getIdentifier);
        
        this.upToDate = upToDate;
        this.getIdentifier = getIdentifier;
    }

    
    public UpToDateTargetSpec(UpToDateTargetSpec<CONTEXT, TARGET> other,
            List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {
        super(other, additionalPrerequisites);
        
        this.upToDate = other.upToDate;
        this.getIdentifier = other.getIdentifier;
    }


    @Override
    TargetDefinition<TARGET> createTargetDefinition(
            LogContext logContext,
            CONTEXT context,
            TARGET target,
            List<Prerequisites> prerequisitesList) {
        
        return new UpToDateTarget<>(
                logContext,
                getIdentifier.apply(target),
                new TargetKey<>(getType(), target),
                getDescriptionFunction().apply(target),
                prerequisitesList,
                upToDate,
                makeAction(),
                makeActionWithResult());
    }

    @Override
    public TargetSpec<CONTEXT, TARGET> addPrerequisiteSpecs(List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

        return new UpToDateTargetSpec<>(this, additionalPrerequisites);
    }
}
