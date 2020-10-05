package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface TypedSubTargetBuilder<CONTEXT extends TaskContext, TARGET> 
	extends SubTargetBuilder<CONTEXT, TARGET, PrerequisitesOrActionBuilder<CONTEXT,TARGET>> {

}
