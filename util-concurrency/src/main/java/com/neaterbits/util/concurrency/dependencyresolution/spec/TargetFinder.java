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
			findTargets(
			        new Config<>(logContext, context, logger),
			        targetSpec,
			        null,
			        0,
			        rootTarget);
		}

		asyncExecutor.runQueuedResultRunnables();
	}

	@Override
	<CONTEXT extends TaskContext, TARGET>
		void findTargets(
		        Config<CONTEXT> config,
		        TargetSpec<CONTEXT, TARGET> targetSpec,
				TARGET target,
				int indent,
				Consumer<TargetDefinition<TARGET>> targetCreated) {
		
		if (config.logger != null) {
			config.logger.onFindTarget(indent, config.context, targetSpec, target);
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
			
				final TargetDefinition<TARGET> createdTarget = targetSpec.createTargetDefinition(
				        config.logContext,
				        config.context,
				        target,
				        prerequisites);
				
				if (config.logger != null) {
					config.logger.onFoundPrerequisites(indent, createdTarget, prerequisites);
				}
				
				createdTargetDefinition = createdTarget;
			}

			targetCreated.accept(createdTargetDefinition);
		};
		
		findPrerequisites(
		        config,
		        targetSpec,
				target,
				indent + 1,
				onFoundPrerequisites);
	}

	private <CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE> void findPrerequisites(
	        Config<CONTEXT> config,
	        TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			int indent,
			Consumer<List<Prerequisites>> onResult) {

	    final List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisiteSpecs = targetSpec.getPrerequisiteSpecs();
	    
		final List<Prerequisites> list = new ArrayList<>(prerequisiteSpecs.size());

		if (prerequisiteSpecs.isEmpty()) {
			onResult.accept(list);
		}
		else {
			for (PrerequisiteSpec<CONTEXT, TARGET, ?> prerequisiteSpec : prerequisiteSpecs) {
	
				getPrerequisites(config, targetSpec, target, prerequisiteSpec, indent, prerequisitesList -> {
					
					// System.out.println("## find prerequisites for " + target);
					
					final Prerequisites prerequisites = new Prerequisites(
							config.logContext,
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
