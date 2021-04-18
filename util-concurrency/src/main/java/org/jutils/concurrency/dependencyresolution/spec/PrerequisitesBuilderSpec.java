package org.jutils.concurrency.dependencyresolution.spec;

import org.jutils.concurrency.dependencyresolution.spec.builder.PrerequisitesBuilder;
import org.jutils.concurrency.scheduling.task.TaskContext;

public abstract class PrerequisitesBuilderSpec<CONTEXT extends TaskContext, TARGET> {

	public abstract void buildSpec(PrerequisitesBuilder<CONTEXT, TARGET> builder);
	
}
