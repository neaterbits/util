package org.jutils.concurrency.dependencyresolution.executor.logger;

import org.jutils.concurrency.dependencyresolution.executor.CollectedProduct;
import org.jutils.concurrency.dependencyresolution.executor.CollectedProducts;
import org.jutils.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import org.jutils.concurrency.dependencyresolution.executor.Status;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionLog;

public interface TargetExecutorLogger {

    default String targetString(TargetDefinition<?> target) {
        return target.getDebugString();
    }

    default String targetLogString(TargetDefinition<?> target) {
        return target.getDebugString();
    }

	void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState);

	void onStateChange(TargetDefinition<?> target, String oldState, String newState);
	
	void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget);
	
	void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status);
	
	void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects);

	void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects subTargetObjects);

	void onScheduleTarget(TargetDefinition<?> target, Status hasCompletedPrerequisites, TargetExecutorLogState logState);

	void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected, TargetExecutorLogState logState);

	void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects, CollectedProduct collected, TargetExecutorLogState logState);
	
	void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog);

	void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception);
	
	void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState);
}
