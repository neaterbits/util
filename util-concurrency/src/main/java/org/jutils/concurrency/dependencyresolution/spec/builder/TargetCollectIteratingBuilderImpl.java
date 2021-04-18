package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.executor.SubPrerequisites;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

class TargetCollectIteratingBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PRODUCT, ITEM>
	implements TargetCollectIteratingBuilder<CONTEXT, TARGET, PRODUCT, ITEM> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	private final PrerequisiteBuilderState<CONTEXT, TARGET, PRODUCT, ITEM> prerequisiteBuilderState;
	
	TargetCollectIteratingBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description, Class<PRODUCT> productType, Class<ITEM> itemType) {
		
		this.targetBuilderState = targetBuilderState;
		this.prerequisiteBuilderState = new PrerequisiteBuilderState<>(description, productType, itemType);
		
		targetBuilderState.addPrerequisiteBuilder(prerequisiteBuilderState);
	}
	
	@Override
	public <PREREQUISITE> PrerequisiteCollectActionBuilder<CONTEXT, TARGET, PREREQUISITE, PRODUCT, ITEM> fromIterating(
			Constraint constraint,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites) {
		
		prerequisiteBuilderState.setIterating(constraint, getPrerequisites);
		
		return new PrerequisiteCollectActionBuilderImpl<>(targetBuilderState, prerequisiteBuilderState);
	}


	@Override
	public <PREREQUISITE, SUB_TARGET> PrerequisiteCollectActionBuilder<CONTEXT, TARGET, PREREQUISITE, PRODUCT, ITEM> fromIteratingAndBuildingRecursively(
			Constraint constraint, Class<SUB_TARGET> subTargetType,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			BiFunction<CONTEXT, SUB_TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, SUB_TARGET> getDependencyFromPrerequisite) {

		prerequisiteBuilderState.setIteratingAndBuildingRecursively(constraint, getPrerequisites, getSubPrerequisites, getDependencyFromPrerequisite);;
		
		return new PrerequisiteCollectActionBuilderImpl<>(targetBuilderState, prerequisiteBuilderState);
	}
}
