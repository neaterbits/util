package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesItemBuilder<CONTEXT extends TaskContext, TARGET, PRODUCT, ITEM> 
	extends TargetCollectIteratingBuilder<CONTEXT, TARGET, PRODUCT, ITEM> {

}
