package org.jutils.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.InfoTarget;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class InfoTargetSpec<CONTEXT extends TaskContext, TARGET> extends TargetSpec<CONTEXT, TARGET> {
	
	private final Function<TARGET, String> getIdentifier;

	public InfoTargetSpec(
			Class<TARGET> type,
			String semanticType,
            String semanticAction,
			Function<TARGET, String> getIdentifier,
			Function<TARGET, String> getDescription,
			List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites,
			Constraint constraint,
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

		Objects.requireNonNull(type);
		Objects.requireNonNull(getIdentifier);

		this.getIdentifier = getIdentifier;
		
	}

	private InfoTargetSpec(InfoTargetSpec<CONTEXT, TARGET> other, List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

		super(other, additionalPrerequisites);
	
		this.getIdentifier = other.getIdentifier;
	}
	
	
	@Override
	public TargetSpec<CONTEXT, TARGET> addPrerequisiteSpecs(List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

		return new InfoTargetSpec<>(this, additionalPrerequisites);
	}
	
	@Override
	TargetDefinition<TARGET> createTargetDefinition(LogContext logContext, CONTEXT context, TARGET target, List<Prerequisites> prerequisitesList) {
		
		return new InfoTarget<>(
				logContext,
				getType(),
				getSemanticType(),
				getIdentifier.apply(target),
                getSemanticAction(),
				getDescriptionFunction().apply(target),
				target,
				prerequisitesList,
				makeAction(),
				makeActionWithResult());
	}
}
