package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.dependencyresolution.spec.TargetBuilderSpec;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetBuilder<CONTEXT extends TaskContext> {

	NoTargetPrerequisitesBuilder<CONTEXT> addTarget(
	                                        String targetName,
	                                        String semanticType,
	                                        String semanticAction,
	                                        String description);
	
	NoTargetTargetBuildSpecPrerequisitesBuilder addTarget(TargetBuilderSpec<CONTEXT> subTarget);
}
