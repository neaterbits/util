package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.Function;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetPrerequisiteBuilder<CONTEXT extends TaskContext, TARGET> {

	<PREREQUISITE>
	PrerequisiteFromBuilder<CONTEXT, PREREQUISITE> from(Function<TARGET, PREREQUISITE> from);
	
}
