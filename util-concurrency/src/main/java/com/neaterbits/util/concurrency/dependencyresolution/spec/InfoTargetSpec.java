package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.InfoTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionFunction;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

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
