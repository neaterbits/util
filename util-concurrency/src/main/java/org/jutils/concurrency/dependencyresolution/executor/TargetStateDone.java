package org.jutils.concurrency.dependencyresolution.executor;

import org.jutils.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

final class TargetStateDone<CONTEXT extends TaskContext> extends BaseTargetState<CONTEXT> {

	TargetStateDone(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
	}

	@Override
	Status getStatus() {
		return Status.SUCCESS;
	}
}
