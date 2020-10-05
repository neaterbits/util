package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface TargetPrerequisitesBuilder<CONTEXT extends TaskContext, TARGET>
	extends TargetIteratingBuilder<CONTEXT, TARGET> {

	<PRODUCT>
	PrerequisitesProductBuilder<CONTEXT, TARGET, PRODUCT> makingProduct(Class<PRODUCT> productType);
	
}
