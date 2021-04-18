package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface ResultSubTargetBuilder<CONTEXT extends TaskContext, TARGET, RESULT> 
	extends SubTargetBuilder<CONTEXT, TARGET, ResultTypedPrerequisitesBuilder<CONTEXT,TARGET, RESULT>> {

}
