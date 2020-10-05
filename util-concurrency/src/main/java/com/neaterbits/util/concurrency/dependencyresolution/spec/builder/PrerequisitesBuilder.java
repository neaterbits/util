package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesBuilder<CONTEXT extends TaskContext, TARGET> {

	TargetPrerequisitesBuilder<CONTEXT, TARGET> withPrerequisites(String description);

	TargetPrerequisiteBuilder<CONTEXT, TARGET> withPrerequisite(String description);

}
