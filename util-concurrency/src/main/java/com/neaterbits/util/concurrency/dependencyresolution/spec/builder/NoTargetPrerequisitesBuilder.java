package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface NoTargetPrerequisitesBuilder<CONTEXT extends TaskContext> {

	NoTargetIteratingBuilder<CONTEXT> withPrerequisites(String description);

	NoTargetIteratingBuilder<CONTEXT> prerequisite(String description);
	
}
