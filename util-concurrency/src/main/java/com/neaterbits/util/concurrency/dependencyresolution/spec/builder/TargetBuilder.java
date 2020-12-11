package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetBuilderSpec;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface TargetBuilder<CONTEXT extends TaskContext> {

	NoTargetPrerequisitesBuilder<CONTEXT> addTarget(
	                                        String targetName,
	                                        String semanticType,
	                                        String semanticAction,
	                                        String description);
	
	NoTargetTargetBuildSpecPrerequisitesBuilder addTarget(TargetBuilderSpec<CONTEXT> subTarget);
}
