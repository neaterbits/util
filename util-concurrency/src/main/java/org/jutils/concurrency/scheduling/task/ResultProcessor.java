package org.jutils.concurrency.scheduling.task;

public interface ResultProcessor<R> {

	void process(R result);
	
}
