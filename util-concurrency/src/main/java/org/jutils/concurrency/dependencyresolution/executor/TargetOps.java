package org.jutils.concurrency.dependencyresolution.executor;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetOps<CONTEXT extends TaskContext> {

	BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context);
	
	BaseTargetState<CONTEXT> onActionPerformed(TargetExecutionContext<CONTEXT> context);

	BaseTargetState<CONTEXT> onActionError(TargetExecutionContext<CONTEXT> context, Exception ex);

	BaseTargetState<CONTEXT> onActionWithResultPerformed();
	
}
