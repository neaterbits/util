package org.jutils.concurrency.scheduling.task;

public interface ProcessResult<CONTEXT extends TaskContext, T, R> {

	void process(CONTEXT context, T t, R r);

}
