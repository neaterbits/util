package org.jutils.concurrency.dependencyresolution.spec.builder;

import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface ActionBuilder<CONTEXT extends TaskContext, TARGET> {

    void action(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction);

}
