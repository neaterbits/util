package org.jutils.concurrency.dependencyresolution.executor;

import org.jutils.concurrency.statemachine.StateMachine;

public class TargetExecutorStateMachine extends StateMachine<BaseTargetExecutorState> {

	public TargetExecutorStateMachine(BaseTargetExecutorState initialState) {
		super(initialState);
	}

	@Override
	protected String getObjectDebugString() {
		return null;
	}

}
