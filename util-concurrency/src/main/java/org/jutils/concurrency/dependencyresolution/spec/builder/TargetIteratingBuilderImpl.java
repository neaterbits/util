package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.executor.SubPrerequisites;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

class TargetIteratingBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
	implements TargetIteratingBuilder<CONTEXT, TARGET> {

	private final String description;
	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	
	TargetIteratingBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description) {
		
		Objects.requireNonNull(targetBuilderState);
		
		this.targetBuilderState = targetBuilderState;
		this.description = description;
	}
	
	final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> getTargetBuilderState() {
		return targetBuilderState;
	}

	@Override
	public final <PREREQUISITE> PrerequisiteActionOrTargetActionBuilder<CONTEXT, TARGET, PREREQUISITE> fromIterating(
			Constraint constraint,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites) {

		final PrerequisiteBuilderState<CONTEXT, TARGET, Void, Void> prerequisiteBuilderState = new PrerequisiteBuilderState<>(description, null, null);
		
		prerequisiteBuilderState.setIterating(constraint, getPrerequisites);
		
		targetBuilderState.addPrerequisiteBuilder(prerequisiteBuilderState);
		
		return new PrerequisiteActionOrTargetActionBuilderImpl<>(targetBuilderState, prerequisiteBuilderState);
	}

	@Override
	public <PREREQUISITE, SUB_TARGET> PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE>
		fromIteratingAndBuildingRecursively(
				
			Constraint constraint,
			Class<SUB_TARGET> subTargetType,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			BiFunction<CONTEXT, SUB_TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, SUB_TARGET> getDependencyFromPrerequisite) {

		final PrerequisiteBuilderState<CONTEXT, TARGET, Void, Void> prerequisiteBuilderState = new PrerequisiteBuilderState<>(description, null, null);
		
		prerequisiteBuilderState.setIteratingAndBuildingRecursively(
				constraint,
				getPrerequisites,
				getSubPrerequisites,
				getDependencyFromPrerequisite);

		targetBuilderState.addPrerequisiteBuilder(prerequisiteBuilderState);

		return new PrerequisiteActionBuilderImpl<>(targetBuilderState, prerequisiteBuilderState);
	}
}
