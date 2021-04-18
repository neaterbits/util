package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.Consumer;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisiteCollectActionBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITE, PRODUCT, ITEM> {

	PrerequisitesOrCollectBuilder<CONTEXT, TARGET, PRODUCT, ITEM>
		buildBy(Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets);

}
