package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;

public interface UpToDate<TARGET> {

    boolean isUpToDate(TARGET target, Collection<Prerequisites> prerequisites);
    
}
