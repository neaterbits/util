package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface UpToDate<CONTEXT extends TaskContext, TARGET> {

    boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites);
    
}
