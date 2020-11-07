package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.NamedTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionFunction;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class NamedTargetSpec<CONTEXT extends TaskContext> extends TargetSpec<CONTEXT, String> {

    private String name;
    
    public NamedTargetSpec(
            String name,
            String description,
            List<PrerequisiteSpec<CONTEXT, String, ?>> prerequisites,
            Constraint constraint,
            ActionFunction<CONTEXT, String> actionFunction,
            ActionWithResultFunction<CONTEXT, String, ?> actionWithResult,
            ProcessResult<CONTEXT, String, ?> onResult) {
        
        super(null, target -> description, prerequisites, constraint, actionFunction, actionWithResult, onResult);

        Objects.requireNonNull(name);

        this.name = name;
    }

    private NamedTargetSpec(NamedTargetSpec<CONTEXT> other, List<PrerequisiteSpec<CONTEXT, String, ?>> additionalPrerequisites) {

        super(other, additionalPrerequisites);
    
        this.name = other.name;
    }

    String getName() {
        return name;
    }

    @Override
    TargetDefinition<String> createTargetDefinition(
            LogContext logContext,
            CONTEXT context,
            String target,
            List<Prerequisites> prerequisitesList) {

        return new NamedTarget(
                logContext,
                name,
                getDescription(null),
                prerequisitesList,
                makeAction(),
                makeActionWithResult());
    }

    @Override
    public TargetSpec<CONTEXT, String> addPrerequisiteSpecs(
            List<PrerequisiteSpec<CONTEXT, String, ?>> additionalPrerequisites) {

        return new NamedTargetSpec<CONTEXT>(this, additionalPrerequisites);
    }
}
