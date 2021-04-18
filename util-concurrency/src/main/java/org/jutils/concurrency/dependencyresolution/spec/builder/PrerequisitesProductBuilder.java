package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesProductBuilder<CONTEXT extends TaskContext, TARGET, PRODUCT> {
	
	<ITEM>
	PrerequisitesItemBuilder<CONTEXT, TARGET, PRODUCT, ITEM> fromItemType(Class<ITEM> itemType);
	
}
