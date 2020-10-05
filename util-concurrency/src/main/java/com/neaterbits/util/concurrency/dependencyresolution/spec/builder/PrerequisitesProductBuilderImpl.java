package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisitesProductBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PRODUCT>

	implements PrerequisitesProductBuilder<CONTEXT, TARGET, PRODUCT> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	
	private final String description;
	private final Class<PRODUCT> productType;

	PrerequisitesProductBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description, Class<PRODUCT> productType) {
		
		Objects.requireNonNull(targetBuilderState);
		Objects.requireNonNull(description);
		Objects.requireNonNull(productType);
		
		this.targetBuilderState = targetBuilderState;
		this.description = description;
		this.productType = productType;
	}

	@Override
	public <ITEM> PrerequisitesItemBuilder<CONTEXT, TARGET, PRODUCT, ITEM> fromItemType(Class<ITEM> itemType) {
		return new PrerequisitesItemBuilderImpl<>(targetBuilderState, description, productType, itemType);
	}
}
