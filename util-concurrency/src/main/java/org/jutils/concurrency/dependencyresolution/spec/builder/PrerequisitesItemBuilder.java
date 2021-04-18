package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesItemBuilder<CONTEXT extends TaskContext, TARGET, PRODUCT, ITEM> 
	extends TargetCollectIteratingBuilder<CONTEXT, TARGET, PRODUCT, ITEM> {

}
