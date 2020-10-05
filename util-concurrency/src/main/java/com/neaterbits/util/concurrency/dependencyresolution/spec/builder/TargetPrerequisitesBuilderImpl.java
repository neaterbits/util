package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetPrerequisitesBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
		extends TargetIteratingBuilderImpl<CONTEXT, TARGET, FILE_TARGET>
		implements TargetPrerequisitesBuilder<CONTEXT, TARGET> {

	private final String description;
	
	TargetPrerequisitesBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState, String description) {
		
		super(targetBuilderState, description);
		
		Objects.requireNonNull(description);
		
		this.description = description;
	}

	@Override
	public <PRODUCT> PrerequisitesProductBuilder<CONTEXT, TARGET, PRODUCT> makingProduct(Class<PRODUCT> productType) {
		return new PrerequisitesProductBuilderImpl<>(getTargetBuilderState(), description, productType);
	}
}
