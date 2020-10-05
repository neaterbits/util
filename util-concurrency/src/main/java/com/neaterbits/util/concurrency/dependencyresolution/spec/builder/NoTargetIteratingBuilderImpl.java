package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class NoTargetIteratingBuilderImpl<CONTEXT extends TaskContext> implements NoTargetIteratingBuilder<CONTEXT> {

	private final TargetBuilderState<CONTEXT, Object, Object> targetBuilderState;
	private final String prerequisiteDescription;
	
	NoTargetIteratingBuilderImpl(TargetBuilderState<CONTEXT, Object, Object> targetBuilderState, String prerequisiteDescription) {
		this.targetBuilderState = targetBuilderState;
		this.prerequisiteDescription = prerequisiteDescription;
	}

	@Override
	public <PREREQUISITE> NoTargetPrerequisiteBuilder<CONTEXT, PREREQUISITE> fromIterating(
			Function<CONTEXT, Collection<PREREQUISITE>> getPrerequisites) {

		final PrerequisiteBuilderState<CONTEXT, Object, Void, Void> prerequisiteBuilderState = new PrerequisiteBuilderState<>(prerequisiteDescription, null, null);
		
		prerequisiteBuilderState.setIterating(null, (context, target) -> getPrerequisites.apply(context));
		
		targetBuilderState.addPrerequisiteBuilder(prerequisiteBuilderState);
		
		return new NoTargetPrerequisiteBuilderImpl<>(prerequisiteBuilderState);
	}
}
