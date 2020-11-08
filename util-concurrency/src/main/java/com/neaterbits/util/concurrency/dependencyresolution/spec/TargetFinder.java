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
			Consumer<TargetDefinition<TARGET>> onRootTarget) {

	    final List<TargetDefinition<?>> list = new ArrayList<>();
	    
		for (TargetSpec<CONTEXT, TARGET> targetSpec : targetSpecs) {
			findTargets(
			        new Config<>(logContext, context, logger),
			        targetSpec,
			        null,
			        rootTarget -> {
			            
			            list.add(rootTarget);
			            
			            if (list.size() == targetSpecs.size()) {
			                
			                resolveUnknownTargets(logContext, list);
			                
			                for (TargetDefinition<?> def : list) {
			                 
			                    @SuppressWarnings({ "unchecked", "rawtypes" })
                                final TargetDefinition<TARGET> td = (TargetDefinition)def;
			                    
			                    onRootTarget.accept(td);
			                }
			            }
		            });
		}

		asyncExecutor.runQueuedResultRunnables();
	}

	@Override
	<CONTEXT extends TaskContext, TARGET>
		void findTargets(
		        Config<CONTEXT> config,
		        TargetSpec<CONTEXT, TARGET> targetSpec,
				TARGET target,
				Consumer<TargetDefinition<TARGET>> targetCreated) {
		
		if (config.logger != null) {
			config.logger.onFindTarget(config.indent, config.context, targetSpec, target);
		}

		final Consumer<List<Prerequisites>> onFoundPrerequisites = (List<Prerequisites> prerequisites) -> {

			final TargetDefinition<TARGET> createdTargetDefinition = makeTarget(config, targetSpec, target, prerequisites);

			targetCreated.accept(createdTargetDefinition);
		};
		
		findPrerequisites(
		        config.addIndent(),
		        targetSpec,
				target,
				onFoundPrerequisites);
	}

	private <CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE> void findPrerequisites(
	        Config<CONTEXT> config,
	        TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			Consumer<List<Prerequisites>> onResult) {

	    final List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisiteSpecs = targetSpec.getPrerequisiteSpecs();
	    
		final List<Prerequisites> list = new ArrayList<>(prerequisiteSpecs.size());

		if (prerequisiteSpecs.isEmpty()) {
			onResult.accept(list);
		}
		else {
			for (PrerequisiteSpec<CONTEXT, TARGET, ?> prerequisiteSpec : prerequisiteSpecs) {
	
				getPrerequisites(config, targetSpec, target, prerequisiteSpec, prerequisitesList -> {
					
					list.add(makePrerequisites(config.logContext, prerequisitesList, prerequisiteSpec));
	
					if (list.size() == prerequisiteSpecs.size()) {
						onResult.accept(list);
					}
				});
			}
		}
	}
}
