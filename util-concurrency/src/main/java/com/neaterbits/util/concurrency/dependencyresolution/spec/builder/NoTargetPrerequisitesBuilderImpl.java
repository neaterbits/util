package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class NoTargetPrerequisitesBuilderImpl<CONTEXT extends TaskContext>
	implements NoTargetPrerequisitesBuilder<CONTEXT> {

	private final String targetName;
    
	private final String semanticType;
    private final String semanticAction;

    private final String description;
	
	private TargetBuilderState<CONTEXT, ?, ?> targetBuilderState;
	
	NoTargetPrerequisitesBuilderImpl(String targetName, String semanticType, String semanticAction, String description) {

		Objects.requireNonNull(targetName);
		Objects.requireNonNull(semanticType);
		Objects.requireNonNull(description);
		
		this.targetName = targetName;
		this.semanticType = semanticType;
		this.semanticAction = semanticAction;
		this.description = description;
	}

	@Override
	public NoTargetIteratingBuilder<CONTEXT> withPrerequisites(String description) {
		final TargetBuilderState<CONTEXT, Object, Object> targetBuilderState;
		
		this.targetBuilderState = targetBuilderState = new TargetBuilderState<CONTEXT, Object, Object>(
		                                                                    targetName,
		                                                                    semanticType,
		                                                                    semanticAction,
		                                                                    this.description);

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
