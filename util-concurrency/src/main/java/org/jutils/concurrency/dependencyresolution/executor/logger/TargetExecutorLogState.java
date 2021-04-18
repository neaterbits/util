package org.jutils.concurrency.dependencyresolution.executor.logger;

import java.util.Set;

import org.jutils.concurrency.dependencyresolution.executor.Status;
import org.jutils.concurrency.dependencyresolution.executor.TargetBuildResult;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;

public interface TargetExecutorLogState extends TargetBuildResult {

	Set<TargetDefinition<?>> getToExecuteTargets();

	Set<TargetDefinition<?>> getScheduledTargets();
	
	Set<TargetDefinition<?>> getActionPerformedCollectTargets();

	Status getTargetStatus(TargetDefinition<?> target);
}
