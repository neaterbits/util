package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.dependencyresolution.spec.PrerequisitesBuilderSpec;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisitesOrActionBuilder<CONTEXT extends TaskContext, TARGET>
		extends PrerequisitesBuilder<CONTEXT, TARGET>,
		        ActionBuilder<CONTEXT, TARGET> {
    
    PrerequisitesOrActionBuilder<CONTEXT, TARGET> withNamedPrerequisite(String name);
	
	PrerequisitesOrActionBuilder<CONTEXT, TARGET> withPrerequisites(PrerequisitesBuilderSpec<CONTEXT, TARGET> buildSpec);

	<R> ResultProcessor<CONTEXT, TARGET, R> actionWithResult(Constraint constraint, ActionWithResultFunction<CONTEXT, TARGET, R> function);
}
