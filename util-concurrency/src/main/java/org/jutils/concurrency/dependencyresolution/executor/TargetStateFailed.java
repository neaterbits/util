package org.jutils.concurrency.dependencyresolution.executor;

import java.util.Objects;

import org.jutils.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

public class TargetStateFailed<CONTEXT extends TaskContext> extends BaseTargetState<CONTEXT> {

	private final Exception exception;

	TargetStateFailed(TargetDefinition<?> target, TargetExecutorLogger logger, Exception exception) {
		super(target, logger);

		Objects.requireNonNull(exception);
		
		this.exception = exception;
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	Status getStatus() {
		return Status.FAILED;
	}
}
