package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetReference;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

class PrerequisiteCompleteChecker {

	static <CONTEXT extends TaskContext> PrerequisiteCompletion hasCompletedPrerequisites(
			ExecutorState<CONTEXT> targetState,
			TargetDefinition<?> target) {
		
		final PrerequisiteCompletion completion = hasCompletedPrerequisites(targetState::getTargetCompletion, targetState::getTargetDefinition, targetState::printTargetKeys, target);
		
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
			Function<TargetReference<?>, TargetDefinition<?>> getTargetDefiniton,
			TargetDefinition<?> target) {
		
		return hasCompletedPrerequisites(targetState, getTargetDefiniton, null, target);
	}
		

	private static <CONTEXT extends TaskContext> PrerequisiteCompletion hasCompletedPrerequisites(
			Function<TargetDefinition<?>, PrerequisiteCompletion> targetState,
			Function<TargetReference<?>, TargetDefinition<?>> getTargetDefiniton,
			Runnable printTargetKeys,
			TargetDefinition<?> target) {
		
		for (Prerequisites prerequisites : target.getPrerequisites()) {

			for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {
				
				if (prerequisite.getSubTarget() != null) {
					
					final TargetDefinition<?> targetDefinition = getTargetDefiniton.apply(prerequisite.getSubTarget());
					
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
								getTargetDefiniton,
								printTargetKeys,
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