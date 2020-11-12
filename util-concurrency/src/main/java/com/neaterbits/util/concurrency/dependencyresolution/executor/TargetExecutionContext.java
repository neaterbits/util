package com.neaterbits.util.concurrency.dependencyresolution.executor;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetExecutionContext<CONTEXT extends TaskContext> {

    final CONTEXT context;
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
 
		this.context = context;
		this.state = state;
		this.asyncExecutor = asyncExecutor;
		this.logger = logger;
		this.onResult = onResult;
	}
}
