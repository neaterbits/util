package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;
import com.neaterbits.util.concurrency.statemachine.BaseState;

public abstract class BaseTargetState<CONTEXT extends TaskContext>
		extends BaseState<BaseTargetState<CONTEXT>>
		implements TargetOps<CONTEXT> {

	final TargetDefinition<?> target;
	final TargetExecutorLogger logger;

	abstract Status getStatus();
	
	Exception getException() {
		throw new IllegalStateException();
	}
	
	public BaseTargetState(TargetDefinition<?> target, TargetExecutorLogger logger) {
		Objects.requireNonNull(target);
		
		this.target = target;
		this.logger = logger;
	}
	
	@Override
	public BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context) {
		throw new IllegalStateException("not implemented in state " + getClass().getSimpleName());
	}

	@Override
	public BaseTargetState<CONTEXT> onActionPerformed(TargetExecutionContext<CONTEXT> context) {
		throw new IllegalStateException();
	}
	
	@Override
	public BaseTargetState<CONTEXT> onActionError(TargetExecutionContext<CONTEXT> context, Exception ex) {
		throw new IllegalStateException();
	}

	@Override
	public BaseTargetState<CONTEXT> onActionWithResultPerformed() {
		throw new IllegalStateException();
	}

	final void onCompletedTarget(
			TargetExecutionContext<CONTEXT> context,
			TargetDefinition<?> target,
			Exception exception,
			boolean async) {

		
		if (context.logger != null) {
			context.logger.onTargetDone(target, exception, context.state);
		}
		
		// context.state.onCompletedTarget(target, exception);

		context.state.onCompletedTarget(target.getTargetKey());
	}
}
