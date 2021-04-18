package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.BiFunction;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface ActionWithResultBuilder<CONTEXT extends TaskContext, T> {

	<R> ResultProcessor<CONTEXT, T, R> ioBound(BiFunction<CONTEXT, T, R> process);
	
	<R> ResultProcessor<CONTEXT, T, R> cpuBound(BiFunction<CONTEXT, T, R> process);

}
