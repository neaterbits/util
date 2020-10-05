package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Map;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

public interface TargetBuildResult {

	Set<TargetDefinition<?>> getCompletedTargets();

	Map<TargetDefinition<?>, Exception> getFailedTargets();

}
