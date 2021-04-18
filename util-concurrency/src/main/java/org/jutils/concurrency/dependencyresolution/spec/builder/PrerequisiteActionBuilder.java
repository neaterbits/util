package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.Consumer;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisiteActionBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITE> {

	PrerequisitesOrActionBuilder<CONTEXT, TARGET> buildBy(Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets);
	
}
