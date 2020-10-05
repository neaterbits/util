package com.neaterbits.util.concurrency.scheduling.task;

public interface ProcessResult<CONTEXT extends TaskContext, T, R> {

	void process(CONTEXT context, T t, R r);

}
