package org.jutils.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;

import org.jutils.concurrency.dependencyresolution.model.NamedTarget;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class NamedTargetSpec<CONTEXT extends TaskContext> extends TargetSpec<CONTEXT, String> {

    private String name;
    
    public NamedTargetSpec(
            String name,
            String semanticType,
            String semanticAction,
            String description,
            List<PrerequisiteSpec<CONTEXT, String, ?>> prerequisites,
            Constraint constraint,
            ActionFunction<CONTEXT, String> actionFunction,
            ActionWithResultFunction<CONTEXT, String, ?> actionWithResult,
            ProcessResult<CONTEXT, String, ?> onResult) {
        
        super(
                null,
                semanticType,
                semanticAction,
                target -> description,
                prerequisites,
                constraint,
                actionFunction,
                actionWithResult,
                onResult);

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
                getSemanticType(),
                name,
                getSemanticAction(),
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

    @Override
    public String toString() {
        return "NamedTargetSpec [name=" + name + "]";
    }
}
