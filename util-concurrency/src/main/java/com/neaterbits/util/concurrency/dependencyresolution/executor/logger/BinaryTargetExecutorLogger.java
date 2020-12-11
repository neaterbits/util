package com.neaterbits.util.concurrency.dependencyresolution.executor.logger;

import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProduct;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Status;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionLog;

public final class BinaryTargetExecutorLogger implements TargetExecutorLogger {

	private final LogContext logContext;

	public BinaryTargetExecutorLogger(LogContext logContext) {

		Objects.requireNonNull(logContext);

		this.logContext = logContext;
	}
	
	@Override
	public void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState) {

	}

	@Override
	public void onStateChange(TargetDefinition<?> target, String oldState, String newState) {
		target.debug(logContext,
				"State change target " + targetString(target) + " from " + oldState + " to " + newState);
	}

	@Override
	public void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget) {

		target.debug(logContext, "Add recursive subtarget " + targetString(subTarget) + " from "
				+ targetString(target));
	}

	@Override
	public void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status) {

		target.debug(logContext, "Check recursive subtarget complete " + targetString(target)
				+ " with status " + status + " from prerequisites " + target.getPrerequisites());
	}

	@Override
	public void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects) {

		topOfRecursionTarget.debug(logContext, "Add recursion collected to "
				+ targetString(topOfRecursionTarget) + " from " + targetString(target) + " with objects " + subTargetObjects);

	}

	@Override
	public void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects targetObjects) {

		aboveRecursionTarget.debug(logContext, "Top of recursion collected to " + targetString(aboveRecursionTarget) + " from " + targetObjects + "/" + prerequisites);
		
	}

	@Override
	public void onScheduleTarget(TargetDefinition<?> target, Status status, TargetExecutorLogState logState) {

		target.debug(logContext, "Schedule target " + targetString(target) + ", status=" + status);

	}

	@Override
	public void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected,
			TargetExecutorLogState logState) {

		target.debug(logContext, "Collect " + collected + " to target " + targetString(target) + " from "
				+ subProducts.getCollectedObjects());
	}

	@Override
	public void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects,
			CollectedProduct collected, TargetExecutorLogState logState) {

		target.debug(logContext, "Collect " + collected + " to target " + targetString(target) + " from "
				+ targetObjects.getCollectedObjects());
	}

	@Override
	public void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog) {

		target.debug(logContext, "Action " + targetString(target));

	}

	@Override
	public void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception) {
		target.debug(logContext, "Action failed " + targetString(target));
	}

	@Override
	public void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState) {

		if (exception == null) {
			target.debug(logContext, "Complete " + targetString(target));
		} else {
			target.debug(logContext, "Failed " + targetString(target));
		}
	}

}
