package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetFinder extends PrerequisitesFinder {

	TargetFinder(AsyncExecutor asyncExecutor) {

		super(asyncExecutor);
	}

	<CONTEXT extends TaskContext, TARGET> void computeTargets(
			List<TargetSpec<CONTEXT, TARGET>> targetSpecs,
			LogContext logContext,
			CONTEXT context,
			TargetFinderLogger logger,
			Consumer<TargetDefinition<TARGET>> rootTarget) {

		for (TargetSpec<CONTEXT, TARGET> targetSpec : targetSpecs) {
			findTargets(targetSpec, logContext, context, null, logger, 0, rootTarget);
		}

		asyncExecutor.runQueuedResultRunnables();
	}

	@Override
	<CONTEXT extends TaskContext, TARGET>
		void findTargets(
				TargetSpec<CONTEXT, TARGET> targetSpec,
				LogContext logContext,
				CONTEXT context,
				TARGET target,
				TargetFinderLogger logger,
				int indent,
				Consumer<TargetDefinition<TARGET>> targetCreated) {
		
		if (logger != null) {
			logger.onFindTarget(indent, context, targetSpec, target);
		}

		
		final Consumer<List<Prerequisites>> onFoundPrerequisites = (List<Prerequisites> prerequisites) -> {

			final TargetDefinition<TARGET> createdTargetDefinition;
			
			if (
				   (prerequisites == null || prerequisites.isEmpty())
				&& !targetSpec.hasAction()) {
				
				// Link to target specified elsewhere
			    throw new UnsupportedOperationException();
			}
			else {
			
				final TargetDefinition<TARGET> createdTarget = targetSpec.createTargetDefinition(logContext, context, target, prerequisites);
				
				if (logger != null) {
					logger.onFoundPrerequisites(indent, createdTarget, prerequisites);
				}
				
				createdTargetDefinition = createdTarget;
			}

			targetCreated.accept(createdTargetDefinition);
		};
		
		findPrerequisites(
				logContext,
				context,
				targetSpec,
				target,
				targetSpec.getPrerequisiteSpecs(),
				logger, indent + 1,
				onFoundPrerequisites);
	}

	private <CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE> void findPrerequisites(
			LogContext logContext,
			CONTEXT context,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisiteSpecs,
			TargetFinderLogger logger,
			int indent,
			Consumer<List<Prerequisites>> onResult) {

		final List<Prerequisites> list = new ArrayList<>(prerequisiteSpecs.size());

		if (prerequisiteSpecs.isEmpty()) {
			onResult.accept(list);
		}
		else {
			for (PrerequisiteSpec<CONTEXT, TARGET, ?> prerequisiteSpec : prerequisiteSpecs) {
	
				getPrerequisites(logContext, context, targetSpec, target, prerequisiteSpec, logger, indent, prerequisitesList -> {
					
					// System.out.println("## find prerequisites for " + target);
					
					final Prerequisites prerequisites = new Prerequisites(
							logContext,
							prerequisitesList,
							prerequisiteSpec.getDescription(),
							prerequisiteSpec.getRecursiveBuildInfo(),
							prerequisiteSpec.getCollectors());
					
					list.add(prerequisites);
	
					if (list.size() == prerequisiteSpecs.size()) {
						onResult.accept(list);
					}
				});
			}
		}
	}
}
