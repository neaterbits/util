package org.jutils.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.model.TargetKey;
import org.jutils.concurrency.dependencyresolution.model.UpToDate;
import org.jutils.concurrency.dependencyresolution.model.UpToDateTarget;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class UpToDateTargetSpec<CONTEXT extends TaskContext, TARGET>
    extends TargetSpec<CONTEXT, TARGET> {

    private final UpToDate<CONTEXT, TARGET> upToDate;
    private final Function<TARGET, String> getIdentifier;
    
    public UpToDateTargetSpec(
            Class<TARGET> type,
            String semanticType,
            String semanticAction,
            UpToDate<CONTEXT, TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription,
            List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites, Constraint constraint,
            ActionFunction<CONTEXT, TARGET> actionFunction,
            ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult,
            ProcessResult<CONTEXT, TARGET, ?> onResult) {

        super(
                type,
                semanticType,
                semanticAction,
                getDescription,
                prerequisites,
                constraint,
                actionFunction,
                actionWithResult,
                onResult);

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
                getSemanticType(),
                getIdentifier.apply(target),
                new TargetKey<>(getType(), target),
                getSemanticAction(),
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
