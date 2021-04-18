package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;
import java.util.function.Consumer;

import org.jutils.concurrency.dependencyresolution.spec.BuildSpec;
import org.jutils.concurrency.scheduling.task.TaskContext;

class PrerequisiteCollectActionBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE, PRODUCT, ITEM>
		implements PrerequisiteCollectActionBuilder<CONTEXT, TARGET, PREREQUISITE, PRODUCT, ITEM> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	private final PrerequisiteBuilderState<CONTEXT, TARGET, PRODUCT, ITEM> prerequisiteBuilderState;

	PrerequisiteCollectActionBuilderImpl(
			TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState,
			PrerequisiteBuilderState<CONTEXT, TARGET, PRODUCT, ITEM> prerequisiteBuilderState) {

		Objects.requireNonNull(targetBuilderState);
		Objects.requireNonNull(prerequisiteBuilderState);
		
		this.targetBuilderState = targetBuilderState;
		this.prerequisiteBuilderState = prerequisiteBuilderState;
	}

	@Override
	public PrerequisitesOrCollectBuilder<CONTEXT, TARGET, PRODUCT, ITEM> buildBy(
			Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets) {

		final TypedSubTargetBuilderImpl<CONTEXT, PREREQUISITE> typedSubTargetBuilder = new TypedSubTargetBuilderImpl<>();
		
		prerequisiteTargets.accept(typedSubTargetBuilder);
		
		final TargetBuilderState<CONTEXT, PREREQUISITE, ?> subTargetState = typedSubTargetBuilder.build();
		
		prerequisiteBuilderState.setBuild(new BuildSpec<>(subTargetState.build()));
		
		return new PrerequisitesOrCollectBuilderImpl<>(targetBuilderState, prerequisiteBuilderState);
	}
}
