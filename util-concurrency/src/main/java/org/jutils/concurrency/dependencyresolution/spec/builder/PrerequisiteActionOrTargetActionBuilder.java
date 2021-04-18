package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface PrerequisiteActionOrTargetActionBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITE>
    extends PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE>,
            ActionBuilder<CONTEXT, TARGET> {

}
