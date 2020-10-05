package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class NoTargetPrerequisitesBuilderImpl<CONTEXT extends TaskContext>
	implements NoTargetPrerequisitesBuilder<CONTEXT> {

	private final String targetName;
	private final String description;
	
	private TargetBuilderState<CONTEXT, ?, ?> targetBuilderState;
	
	NoTargetPrerequisitesBuilderImpl(String targetName, String description) {

		Objects.requireNonNull(targetName);
		Objects.requireNonNull(description);
		
		this.targetName = targetName;
		this.description = description;
	}

	@Override
	public NoTargetIteratingBuilder<CONTEXT> withPrerequisites(String description) {
		final TargetBuilderState<CONTEXT, Object, Object> targetBuilderState;
		
		this.targetBuilderState = targetBuilderState = new TargetBuilderState<CONTEXT, Object, Object>(null, targetName, null, target -> this.description);

		return new NoTargetIteratingBuilderImpl<>(targetBuilderState, description);
	}


	@Override
	public NoTargetIteratingBuilder<CONTEXT> prerequisite(String description) {
		throw new UnsupportedOperationException();
	}
	
	TargetSpec<CONTEXT, ?> build() {
		return targetBuilderState.build();
	}
}
