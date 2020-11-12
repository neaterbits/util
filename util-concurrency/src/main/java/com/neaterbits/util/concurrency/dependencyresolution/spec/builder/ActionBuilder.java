package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface ActionBuilder<CONTEXT extends TaskContext, TARGET> {

    void action(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction);

}
