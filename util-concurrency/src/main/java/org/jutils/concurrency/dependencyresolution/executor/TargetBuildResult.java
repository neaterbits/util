package org.jutils.concurrency.dependencyresolution.executor;

import java.util.Map;
import java.util.Set;

import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.PrerequisitesMap;

public interface TargetBuildResult extends PrerequisitesMap {

	Set<TargetDefinition<?>> getCompletedTargets();

	Map<TargetDefinition<?>, Exception> getFailedTargets();

}
