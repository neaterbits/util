package com.neaterbits.util.concurrency.dependencyresolution.executor.logger;

import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProduct;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectedTargetObjects;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Status;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionLog;

public final class PrintlnTargetExecutorLogger implements TargetExecutorLogger {

	@Override
	public void onScheduleTargets(int numScheduledJobs, TargetExecutorLogState logState) {

		System.out.println("Schedule target numScheduledJobs=" + numScheduledJobs);
	}

	@Override
	public void onStateChange(TargetDefinition<?> target, String oldState, String newState) {

		System.out.println("State change target " + target.targetToLogString() + " from " + oldState + " to " + newState);
	}

	@Override
	public void onAddRecursiveTarget(TargetDefinition<?> target, TargetDefinition<?> subTarget) {

		System.out.println("Add recursive subtarget " + subTarget + " from " + target);
	}

	@Override
	public void onCheckRecursiveTargetsComplete(TargetDefinition<?> target, Status status) {

		System.out.println("Check recursive subtarget complete " + target + " with status " + status
				+ " from prerequisites " + target.getPrerequisites());
		
	}

	@Override
	public void onAddSubRecursionCollected(TargetDefinition<?> topOfRecursionTarget, TargetDefinition<?> target, CollectedTargetObjects subTargetObjects) {

		System.out.println("Add recursion collected to " + topOfRecursionTarget.targetSimpleLogString()
			+ " from " + target.targetSimpleLogString() + " with objects " + subTargetObjects);
		
	}

	@Override
	public void onAddTopRecursionCollected(TargetDefinition<?> aboveRecursionTarget, Prerequisites prerequisites, CollectedTargetObjects targetObjects) {

		System.out.println("Top of recursion collected to " + aboveRecursionTarget.targetSimpleLogString() + " from " + targetObjects + "/" + prerequisites);
		
	}

	@Override
	public void onScheduleTarget(TargetDefinition<?> target, Status status, TargetExecutorLogState logState) {

		System.out.println("Schedule target " + target.targetToLogString() + ", status=" + status);
		
	}
	
	@Override
	public void onCollectProducts(TargetDefinition<?> target, CollectedProducts subProducts, CollectedProduct collected,
			TargetExecutorLogState logState) {

		System.out.println("Collect " + collected + " to target " + target.targetToLogString() + " from " + subProducts.getCollectedObjects());
	}

	@Override
	public void onCollectTargetObjects(TargetDefinition<?> target, CollectedTargetObjects targetObjects,
			CollectedProduct collected, TargetExecutorLogState logState) {

		System.out.println("Collect " + collected + " to target " + target.targetToLogString() + " from " + targetObjects.getCollectedObjects());
	}


	@Override
	public void onActionCompleted(TargetDefinition<?> target, TargetExecutorLogState logState, ActionLog actionLog) {

		System.out.println("Action " + target);
		
	}
	
	

	@Override
	public void onActionException(TargetDefinition<?> target, TargetExecutorLogState logState, Exception exception) {
		System.out.println("Action failed " + target);
	}

	@Override
	public void onTargetDone(TargetDefinition<?> target, Exception exception, TargetExecutorLogState logState) {
		
		if (exception == null) {
			System.out.println("Complete " + target.targetToLogString());
		}
		else {
			System.out.println("Failed " + target.targetToLogString());
			
			exception.printStackTrace();
		}
	}
}
