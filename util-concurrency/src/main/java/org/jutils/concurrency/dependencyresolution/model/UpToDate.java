package org.jutils.concurrency.dependencyresolution.model;

import java.util.Collection;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface UpToDate<CONTEXT extends TaskContext, TARGET> {

    boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites);
    
}
