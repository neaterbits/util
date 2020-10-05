package com.neaterbits.util.concurrency.dependencyresolution.spec;

import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.PrerequisitesBuilder;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public abstract class PrerequisitesBuilderSpec<CONTEXT extends TaskContext, TARGET> {

	public abstract void buildSpec(PrerequisitesBuilder<CONTEXT, TARGET> builder);
	
}
