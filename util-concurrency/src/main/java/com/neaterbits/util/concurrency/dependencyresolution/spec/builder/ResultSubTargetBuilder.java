package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface ResultSubTargetBuilder<CONTEXT extends TaskContext, TARGET, RESULT> 
	extends SubTargetBuilder<CONTEXT, TARGET, ResultTypedPrerequisitesBuilder<CONTEXT,TARGET, RESULT>> {

}
