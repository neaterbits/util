package com.neaterbits.util.concurrency.dependencyresolution.executor;

import com.neaterbits.util.concurrency.statemachine.StateMachine;

public class TargetExecutorStateMachine extends StateMachine<BaseTargetExecutorState> {

	public TargetExecutorStateMachine(BaseTargetExecutorState initialState) {
		super(initialState);
	}

	@Override
	protected String getObjectDebugString() {
		return null;
	}

}
