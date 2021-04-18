package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;
import java.util.function.Consumer;

import org.jutils.concurrency.dependencyresolution.spec.BuildSpec;
import org.jutils.concurrency.scheduling.task.TaskContext;

class PrerequisiteActionBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE>
		implements PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisiteBuilderState;
	
	PrerequisiteActionBuilderImpl(
			TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState,
			PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisiteBuilderState) {

		Objects.requireNonNull(targetBuilderState);
		Objects.requireNonNull(prerequisiteBuilderState);
		
		this.targetBuilderState = targetBuilderState;
		this.prerequisiteBuilderState = prerequisiteBuilderState;
	}
	
	final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> getTargetBuilderState() {
        return targetBuilderState;
    }

    @Override
	public final PrerequisitesOrActionBuilder<CONTEXT, TARGET> buildBy(
			Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets) {

		final TypedSubTargetBuilderImpl<CONTEXT, PREREQUISITE> typedSubTargetBuilder = new TypedSubTargetBuilderImpl<>();
		
		prerequisiteTargets.accept(typedSubTargetBuilder);
		
		prerequisiteBuilderState.setBuild(new BuildSpec<>(typedSubTargetBuilder.build().build()));
		
		return new PrerequisitesOrActionBuilderImpl<CONTEXT, TARGET, FILE_TARGET>(targetBuilderState);
	}
}
