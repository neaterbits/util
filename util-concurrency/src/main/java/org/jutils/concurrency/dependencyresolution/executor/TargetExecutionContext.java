package org.jutils.concurrency.dependencyresolution.executor;

import org.jutils.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import org.jutils.concurrency.scheduling.AsyncExecutor;
import org.jutils.concurrency.scheduling.task.TaskContext;

final class TargetExecutionContext<CONTEXT extends TaskContext> {

    final CONTEXT taskContext;
	final ExecutorState<CONTEXT> state;
	final AsyncExecutor asyncExecutor;
	final TargetExecutorLogger logger;
	final OnBuildResult onResult;
	
	TargetExecutionContext(
	        CONTEXT context,
	        ExecutorState<CONTEXT> state,
	        AsyncExecutor asyncExecutor,
	        TargetExecutorLogger logger,
	        OnBuildResult onResult) {
 
		this.taskContext = context;
		this.state = state;
		this.asyncExecutor = asyncExecutor;
		this.logger = logger;
		this.onResult = onResult;
	}
}
