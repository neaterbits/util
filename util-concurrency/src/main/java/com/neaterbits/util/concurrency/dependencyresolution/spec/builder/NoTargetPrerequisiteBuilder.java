package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.function.Consumer;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface NoTargetPrerequisiteBuilder<CONTEXT extends TaskContext, PREREQUISITE> {

	void buildBy(Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets);

	
}
