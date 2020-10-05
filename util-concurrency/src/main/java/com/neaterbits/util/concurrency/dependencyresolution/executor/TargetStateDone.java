package com.neaterbits.util.concurrency.dependencyresolution.executor;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetStateDone<CONTEXT extends TaskContext> extends BaseTargetState<CONTEXT> {

	TargetStateDone(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
	}

	@Override
	Status getStatus() {
		return Status.SUCCESS;
	}
}
