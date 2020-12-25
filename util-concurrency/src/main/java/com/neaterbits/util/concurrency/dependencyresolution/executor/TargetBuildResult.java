package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Map;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.PrerequisitesMap;

public interface TargetBuildResult extends PrerequisitesMap {

	Set<TargetDefinition<?>> getCompletedTargets();

	Map<TargetDefinition<?>, Exception> getFailedTargets();

}
