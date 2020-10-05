package com.neaterbits.util.concurrency.dependencyresolution.executor;


import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;
import com.neaterbits.util.concurrency.statemachine.StateMachine;

public class TargetStateMachineBase<CONTEXT extends TaskContext>
	extends StateMachine<BaseTargetState<CONTEXT>> {

	private final TargetDefinition<?> target;
	
	TargetStateMachineBase(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(new TargetStateToExecute<>(target, logger));
	
		this.target = target;
	}

	@Override
	protected String getObjectDebugString() {
		return target.getDebugString();
	}
}
