package com.neaterbits.util.concurrency.dependencyresolution.executor.logger;

import java.util.Arrays;
import java.util.List;

import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProduct;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Status;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionLog;

public final class DelegatingTargetExecutorLogger implements TargetExecutorLogger {

	private final List<TargetExecutorLogger> delegates;

	public DelegatingTargetExecutorLogger(TargetExecutorLogger ... delegates) {
		this.delegates = Arrays.asList(delegates);
	}
	
	@Override
	public void onStateChange(TargetDefinition<?> target, String oldState, String newState) {
		delegates.forEach(logger -> logger.onStateChange(target, oldState, newState));
	}

	@Override
	public void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget) {
		delegates.forEach(logger -> logger.onAddRecursiveTarget(target, subTarget));
	}

	@Override
	public void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status) {
		delegates.forEach(logger -> logger.onCheckRecursiveTargetsComplete(target, status));
	}

	@Override
	public void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects) {
		delegates.forEach(logger -> logger.onAddSubRecursionCollected(topOfRecursionTarget, target, subTargetObjects));
	}

	@Override
	public void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects subTargetObjects) {
		delegates.forEach(logger -> logger.onAddTopRecursionCollected(aboveRecursionTarget, prerequisites, subTargetObjects));
	}

	@Override
	public void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState) {
		delegates.forEach(logger -> logger.onScheduleTargets(numScheduledJobs, logState));
	}

	@Override
	public void onScheduleTarget(TargetDefinition<?> target, Status hasCompletedPrerequisites, TargetExecutorLogState logState) {
		delegates.forEach(logger -> logger.onScheduleTarget(target, hasCompletedPrerequisites, logState));
	}

	@Override
	public void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected,
			TargetExecutorLogState logState) {

		delegates.forEach(logger -> logger.onCollectProducts(target, subProducts, collected, logState));
	}

	@Override
	public void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects,
			CollectedProduct collected, TargetExecutorLogState logState) {

		delegates.forEach(logger -> logger.onCollectTargetObjects(target, targetObjects, collected, logState));
	}

	@Override
	public void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog) {

		delegates.forEach(logger -> logger.onActionCompleted(target, logState, actionLog));
	}

	@Override
	public void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception) {

		delegates.forEach(logger -> logger.onActionException(target, logState, exception));
	}

	@Override
	public void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState) {

		delegates.forEach(logger -> logger.onTargetDone(target, exception, logState));
	}
}
