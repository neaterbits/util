package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface NoTargetPrerequisitesBuilder<CONTEXT extends TaskContext> {
    
    NoTargetPrerequisitesBuilder<CONTEXT> withNamedPrerequisite(String name);

	NoTargetIteratingBuilder<CONTEXT> withPrerequisites(String description);

	NoTargetIteratingBuilder<CONTEXT> prerequisite(String description);
	
}
