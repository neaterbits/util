package org.jutils.concurrency.statemachine;

public interface StateOperation<STATE> {

	STATE execute(STATE curState);
	
}
