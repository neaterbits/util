package com.neaterbits.util.concurrency.statemachine;

public interface StateOperation<STATE> {

	STATE execute(STATE curState);
	
}
