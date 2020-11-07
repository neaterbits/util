package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

abstract class PrerequisitesFinder extends TargetSpecApplier {

	final AsyncExecutor asyncExecutor;
	
	PrerequisitesFinder(AsyncExecutor asyncExecutor) {
		
		Objects.requireNonNull(asyncExecutor);

		this.asyncExecutor = asyncExecutor;
	}

	abstract <CONTEXT extends TaskContext, TARGET>
	void findTargets(
	        Config<CONTEXT> config,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			int indent,
			Consumer<TargetDefinition<TARGET>> targetCreated);
	
	final <CONTEXT extends TaskContext, TARGET, PREREQUISITE>
	void getPrerequisites(
            Config<CONTEXT> config,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			int indent,
			Consumer<Set<Prerequisite<?>>> listener) {
		
		if (prerequisiteSpec.getConstraint() != null) {

			asyncExecutor.schedule(
					prerequisiteSpec.getConstraint(),
					null,
					param -> {
						return prerequisiteSpec.getPrerequisites(config.context, target);
					},
					(param, result) -> {

						getPrerequisites(config, prerequisiteSpec, result, indent, prerequisites -> {
						
							if (config.logger != null) {
								config.logger.onPrerequisites(indent, targetSpec, target, prerequisiteSpec, prerequisites);
							}
														
							listener.accept(prerequisites);
						});
					});
		} else {
			final Collection<PREREQUISITE> sub = prerequisiteSpec.getPrerequisites(config.context, target);

			getPrerequisites(config, prerequisiteSpec, sub, indent, prerequisites -> {
				
				if (config.logger != null) {
					config.logger.onPrerequisites(indent, targetSpec, target, prerequisiteSpec, prerequisites);
				}
				
				listener.accept(prerequisites);
			});
		}
	}

	private <CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE>
	void getPrerequisites(
	        Config<CONTEXT> config,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			Collection<PREREQUISITE> sub,
			int indent,
			Consumer<Set<Prerequisite<?>>> listener) {

		if (sub == null) {
			throw new IllegalStateException("No prerequisites for " + prerequisiteSpec.getDescription());
		}
		
		final Set<PREREQUISITE> subSet = sub instanceof Set<?> ? (Set<PREREQUISITE>)sub : new HashSet<>(sub);
		
		final Set<Prerequisite<?>> prerequisiteSet = new HashSet<>(subSet.size());

		if (sub.isEmpty()) {
			listener.accept(prerequisiteSet);
		}
		else {
		
			for (PREREQUISITE prerequisite : subSet) {
				
				if (prerequisiteSpec.getAction() != null) {
					
					final TargetSpec<CONTEXT, PREREQUISITE> subTargetSpec = prerequisiteSpec.getAction().getSubTarget();
					
					findTargets(
					        config,
					        subTargetSpec,
							prerequisite,
							indent + 1,
							subTarget -> {
	
						final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(config.logContext, prerequisite, subTarget);
						
						prerequisiteSet.add(subPrerequisite);
						
						if (prerequisiteSet.size() == subSet.size()) {
							listener.accept(prerequisiteSet);
						}
					});
				}
				else {
					
					// Only prerequisite, no action to build target so probably a source file

					/*
					final TargetReference<PREREQUISITE> targetReference = new TargetReference<PREREQUISITE>(
							logContext,
							null,
							prerequisite,
							null);
					
					final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(logContext, prerequisite, targetReference);
					*/

					final File sourceFile = prerequisiteSpec.getSingleFileFunction().apply(prerequisite);
					
					final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(config.logContext, prerequisite, sourceFile);
					
					prerequisiteSet.add(subPrerequisite);
	
					if (prerequisiteSet.size() == sub.size()) {
						listener.accept(prerequisiteSet);
					}
				}
			}
		}
	}
}
