package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface PrerequisiteActionOrTargetActionBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITE>
    extends PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE>,
            ActionBuilder<CONTEXT, TARGET> {

}
