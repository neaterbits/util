package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface ResultProcessor<CONTEXT extends TaskContext, T, R> {

	void processResult(ProcessResult<CONTEXT, T, R> onResult);
}
