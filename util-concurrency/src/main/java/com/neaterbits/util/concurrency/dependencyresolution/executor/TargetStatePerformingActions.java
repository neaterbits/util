package com.neaterbits.util.concurrency.dependencyresolution.executor;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetStatePerformingActions<CONTEXT extends TaskContext> extends BaseTargetState<CONTEXT> {

	TargetStatePerformingActions(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
	}

	@Override
	public BaseTargetState<CONTEXT> onActionPerformed(TargetExecutionContext<CONTEXT> context) {

	    return testForRecursiveTargets(context);
	}
	
	@Override
	public BaseTargetState<CONTEXT> onActionError(TargetExecutionContext<CONTEXT> context, Exception ex) {

		onCompletedTarget(context, target, ex, false);
		
		return new TargetStateFailed<>(target, logger, ex);
	}

	@Override
	Status getStatus() {
		return Status.SCHEDULED;
	}

	@Override
	public BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context) {

		// not applicable to this state
		
		return this;
	}
}
