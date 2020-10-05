package com.neaterbits.util.concurrency.statemachine;

import java.util.Objects;

public abstract class BaseState<STATE extends BaseState<STATE>> {

	private StateMachine<STATE> stateMachine;
	
	final void init(StateMachine<STATE> stateMachine) {
		
		Objects.requireNonNull(stateMachine);
		
		if (this.stateMachine != null) {
			throw new IllegalStateException();
		}
		
		this.stateMachine = stateMachine;
	}
	
	protected final void schedule(StateOperation<STATE> stateOperation) {
		stateMachine.schedule(stateOperation);
	}
	
	protected void onEnter() {
		
	}
}
