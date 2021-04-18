package org.jutils.concurrency.dependencyresolution.executor;


import org.jutils.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.concurrency.statemachine.StateMachine;

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
