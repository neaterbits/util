package com.neaterbits.util.concurrency.dependencyresolution.executor.logger;

import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProduct;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Status;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionLog;

public class PrintlnTargetExecutorLogger implements TargetExecutorLogger {

	@Override
	public final void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState) {

		System.out.println("Schedule target numScheduledJobs=" + numScheduledJobs);
	}

	@Override
	public final void onStateChange(TargetDefinition<?> target, String oldState, String newState) {

		System.out.println("State change target " + targetLogString(target) + " from " + oldState + " to " + newState);
	}

	@Override
	public final void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget) {

		System.out.println("Add recursive subtarget " + subTarget + " from " + target);
	}

	@Override
	public final void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status) {

		System.out.println("Check recursive subtarget complete " + target + " with status " + status
				+ " from prerequisites " + target.getPrerequisites());
		
	}

	@Override
	public final void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects) {

		System.out.println("Add recursion collected to " + targetString(topOfRecursionTarget)
			+ " from " + targetString(target) + " with objects " + subTargetObjects);
		
	}

	@Override
	public final void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects targetObjects) {

		System.out.println("Top of recursion collected to " + targetString(aboveRecursionTarget) + " from " + targetObjects + "/" + prerequisites);
		
	}

	@Override
	public final void onScheduleTarget(TargetDefinition<?> target, Status status, TargetExecutorLogState logState) {

		System.out.println("Schedule target " + targetLogString(target) + ", status=" + status);
		
	}
	
	@Override
	public final void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected,
			TargetExecutorLogState logState) {

		System.out.println("Collect " + collected + " to target " + targetLogString(target) + " from " + subProducts.getCollectedObjects());
	}

	@Override
	public final void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects,
			CollectedProduct collected, TargetExecutorLogState logState) {

		System.out.println("Collect " + collected + " to target " + targetLogString(target) + " from " + targetObjects.getCollectedObjects());
	}

	@Override
	public final void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog) {

		System.out.println("Action " + target);
		
	}

	@Override
	public void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception) {
		System.out.println("Action failed " + target);
	}

	@Override
	public void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState) {
		
		if (exception == null) {
			System.out.println("Complete " + targetLogString(target));
		}
		else {
			System.out.println("Failed " + targetLogString(target));
			
			exception.printStackTrace();
		}
	}
}
