package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.Consumer;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface NoTargetPrerequisiteBuilder<CONTEXT extends TaskContext, PREREQUISITE> {

	void buildBy(Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets);

	
}
