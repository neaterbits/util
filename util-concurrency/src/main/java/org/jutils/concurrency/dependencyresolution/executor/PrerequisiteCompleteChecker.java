package org.jutils.concurrency.dependencyresolution.executor;

import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.Prerequisite;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

class PrerequisiteCompleteChecker {

	static <CONTEXT extends TaskContext> PrerequisiteCompletion hasCompletedPrerequisites(
			ExecutorState<CONTEXT> targetState,
			TargetDefinition<?> target) {
		
		final PrerequisiteCompletion completion = hasCompletedPrerequisites(targetState::getTargetCompletion, null, target);
		
		/*
		if (completion.getStatus() == Status.FAILED) {
			System.out.println("## failed target " + target.targetSimpleLogString() + ": " + completion.getException());
			
			completion.getException().printStackTrace();
		}
		*/
		
		return completion;
	}

	static <CONTEXT extends TaskContext> PrerequisiteCompletion hasCompletedPrerequisites(
			Function<TargetDefinition<?>, PrerequisiteCompletion> targetState,
			TargetDefinition<?> target) {
		
		return hasCompletedPrerequisites(targetState, null, target);
	}
		

	private static <CONTEXT extends TaskContext> PrerequisiteCompletion hasCompletedPrerequisites(
			Function<TargetDefinition<?>, PrerequisiteCompletion> targetState,
			Runnable printTargetKeys,
			TargetDefinition<?> target) {
		
		for (Prerequisites prerequisites : target.getPrerequisites()) {

			for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {
				
				if (prerequisite.getSubTarget() != null) {
					
					final TargetDefinition<?> targetDefinition = prerequisite.getSubTarget();
					
					if (targetDefinition == null) {
						
						if (printTargetKeys != null) {
							printTargetKeys.run();
						}

						throw new IllegalStateException("No target definition found for target reference " + prerequisite.getSubTarget()
									+ " of type " + prerequisite.getSubTarget().getTargetObject().getClass().getName());
					}
					else {
					
						final PrerequisiteCompletion subStatus = hasCompletedPrerequisites(
								targetState,
								null,
								targetDefinition);
						
						if (subStatus.getStatus() != Status.SUCCESS) {
	
							return subStatus;
						}
	
						final PrerequisiteCompletion thisStatus = targetState.apply(targetDefinition);
						if (thisStatus.getStatus() != Status.SUCCESS) {
							return thisStatus;
						}
					}
				}
			}
		}
		
		return new PrerequisiteCompletion(Status.SUCCESS);
	}
}
