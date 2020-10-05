package com.neaterbits.util.concurrency.scheduling.task;

public interface ResultProcessor<R> {

	void process(R result);
	
}
