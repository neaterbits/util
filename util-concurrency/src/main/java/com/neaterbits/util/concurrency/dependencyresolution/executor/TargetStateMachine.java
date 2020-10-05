package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetStateMachine<CONTEXT extends TaskContext> extends TargetStateMachineBase<CONTEXT> {

	private final TargetDefinition<?> target;
	private final TargetExecutorLogger logger;
	// private Status status;
	// private Exception exception;
	
	TargetStateMachine(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
		
		Objects.requireNonNull(target);
		
		this.target = target;
		this.logger = logger;
		
		// this.status = Status.TO_EXECUTE;
	}
	
	TargetDefinition<?> getTarget() {
		return target;
	}

	Exception getException() {
		return getCurState().getException();
	}

	Status getStatus() {
		return getCurState().getStatus();
	}
	
	@Override
	protected void onStateChange(BaseTargetState<CONTEXT> curState, BaseTargetState<CONTEXT> nextState) {

		logger.onStateChange(target, curState.getClass().getSimpleName(), nextState.getClass().getSimpleName());
	}

	@Override
	public String toString() {
		return "TargetState [target=" + target.getDebugString() + ", state=" + getCurStateName() + "]";
	}
}
