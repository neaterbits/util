package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.BiConsumer;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface ActionWithoutResultBuilder<CONTEXT extends TaskContext, T> {
	
	void ioBound(BiConsumer<CONTEXT, T> process);

	void cpuBound(BiConsumer<CONTEXT, T> process);

}
