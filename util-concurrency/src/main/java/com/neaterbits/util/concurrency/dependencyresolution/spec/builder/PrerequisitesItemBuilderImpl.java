package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisitesItemBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PRODUCT, ITEM>
		extends TargetCollectIteratingBuilderImpl<CONTEXT, TARGET, FILE_TARGET, PRODUCT, ITEM>
		implements PrerequisitesItemBuilder<CONTEXT, TARGET, PRODUCT, ITEM> {

	PrerequisitesItemBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description, Class<PRODUCT> productType, Class<ITEM> itemType) {
		super(targetBuilderState, description, productType, itemType);
	}
}
