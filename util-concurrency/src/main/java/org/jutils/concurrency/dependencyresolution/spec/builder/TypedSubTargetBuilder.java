package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TypedSubTargetBuilder<CONTEXT extends TaskContext, TARGET> 
	extends SubTargetBuilder<CONTEXT, TARGET, PrerequisitesOrActionBuilder<CONTEXT,TARGET>> {

}
