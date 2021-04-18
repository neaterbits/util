package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesBuilder<CONTEXT extends TaskContext, TARGET> {

	TargetPrerequisitesBuilder<CONTEXT, TARGET> withPrerequisites(String description);

	TargetPrerequisiteBuilder<CONTEXT, TARGET> withPrerequisite(String description);

}
