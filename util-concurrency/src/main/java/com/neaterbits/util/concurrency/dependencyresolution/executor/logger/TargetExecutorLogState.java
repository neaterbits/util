package com.neaterbits.util.concurrency.dependencyresolution.executor.logger;

import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.executor.Status;
import com.neaterbits.util.concurrency.dependencyresolution.executor.TargetBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

public interface TargetExecutorLogState extends TargetBuildResult {

	Set<TargetDefinition<?>> getToExecuteTargets();

	Set<TargetDefinition<?>> getScheduledTargets();
	
	Set<TargetDefinition<?>> getActionPerformedCollectTargets();

	Status getTargetStatus(TargetDefinition<?> target);
}
