package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesProductBuilder<CONTEXT extends TaskContext, TARGET, PRODUCT> {
	
	<ITEM>
	PrerequisitesItemBuilder<CONTEXT, TARGET, PRODUCT, ITEM> fromItemType(Class<ITEM> itemType);
	
}
