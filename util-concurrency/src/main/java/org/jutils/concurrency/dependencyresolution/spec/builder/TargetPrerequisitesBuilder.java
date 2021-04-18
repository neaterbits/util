package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetPrerequisitesBuilder<CONTEXT extends TaskContext, TARGET>
	extends TargetIteratingBuilder<CONTEXT, TARGET> {

	<PRODUCT>
	PrerequisitesProductBuilder<CONTEXT, TARGET, PRODUCT> makingProduct(Class<PRODUCT> productType);
	
}
