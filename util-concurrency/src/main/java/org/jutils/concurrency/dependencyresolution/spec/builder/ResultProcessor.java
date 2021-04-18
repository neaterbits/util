package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface ResultProcessor<CONTEXT extends TaskContext, T, R> {

	void processResult(ProcessResult<CONTEXT, T, R> onResult);
}
