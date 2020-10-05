package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetPrerequisiteBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
		implements TargetPrerequisiteBuilder<CONTEXT, TARGET> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	private final String description;

	TargetPrerequisiteBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description) {
		
		Objects.requireNonNull(targetBuilderState);
		Objects.requireNonNull(description);
		
		this.targetBuilderState = targetBuilderState;
		this.description = description;
	}

	@Override
	public <PREREQUISITE> PrerequisiteFromBuilder<CONTEXT, PREREQUISITE> from(Function<TARGET, PREREQUISITE> from) {
	
		final PrerequisiteBuilderState<CONTEXT, TARGET, Void, Void> prerequisiteBuilderState = new PrerequisiteBuilderState<>(description, null, null);

		targetBuilderState.addPrerequisiteBuilder(prerequisiteBuilderState);
		
		return new PrerequisiteFromBuilderImpl<>(description, from, prerequisiteBuilderState);
	}
}
