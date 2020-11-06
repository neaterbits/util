package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.List;
import java.util.function.BiFunction;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisitesOrCollectBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PRODUCT, ITEM>
		extends PrerequisitesBuilderImpl<CONTEXT, TARGET, FILE_TARGET>
		implements PrerequisitesOrCollectBuilder<CONTEXT, TARGET, PRODUCT, ITEM> {

	private final PrerequisiteBuilderState<CONTEXT, TARGET, PRODUCT, ITEM> prerequisiteBuilderState;


	PrerequisitesOrCollectBuilderImpl(
			TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState,
			PrerequisiteBuilderState<CONTEXT, TARGET, PRODUCT, ITEM> prerequisiteBuilderState) {
		
		super(targetBuilderState);

		this.prerequisiteBuilderState = prerequisiteBuilderState;
	}

	
	
	@Override
	public PrerequisitesOrActionBuilder<CONTEXT, TARGET> collectSubTargetsToProduct(
			BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubTargets) {

		prerequisiteBuilderState.setProduceFromSubTargets(produceFromSubTargets);
		
		return new PrerequisitesOrActionBuilderImpl<>(getTargetBuilderState());
	}



	@Override
	public PrerequisitesOrActionBuilder<CONTEXT, TARGET> collectSubProductsToProduct(
			BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubProducts) {
		
		prerequisiteBuilderState.setProduceFromSubProducts(produceFromSubProducts);
		
		return new PrerequisitesOrActionBuilderImpl<>(getTargetBuilderState());
	}
}
