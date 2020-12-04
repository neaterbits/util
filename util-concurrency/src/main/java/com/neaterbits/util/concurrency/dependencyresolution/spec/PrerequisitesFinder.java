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
			Consumer<TargetDefinition<TARGET>> targetCreated);
	
	final <CONTEXT extends TaskContext, TARGET, PREREQUISITE>
	void getPrerequisites(
            Config<CONTEXT> config,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			Consumer<Set<Prerequisite<?>>> listener) {
		
		if (prerequisiteSpec.getConstraint() != null) {

			asyncExecutor.schedule(
					prerequisiteSpec.getConstraint(),
					null,
					param -> {
						return prerequisiteSpec.getPrerequisites(config.context, target);
					},
					(param, result) -> {

					    if (config.logger != null) {
			                config.logger.onGetPrerequisites(
			                        config.indent,
			                        targetSpec.getDescription(target),
			                        result);
			            }

						getPrerequisites(config, prerequisiteSpec, result, prerequisites -> {
						
							if (config.logger != null) {
								config.logger.onPrerequisites(config.indent, targetSpec, target, prerequisiteSpec, prerequisites);
							}
														
							listener.accept(prerequisites);
						});
					});
		} else {
			final Collection<PREREQUISITE> sub = prerequisiteSpec.getPrerequisites(config.context, target);

            if (config.logger != null) {
                config.logger.onGetPrerequisites(config.indent, targetSpec.getDescription(target), sub);
            }

			getPrerequisites(config, prerequisiteSpec, sub, prerequisites -> {
				
				if (config.logger != null) {
					config.logger.onPrerequisites(config.indent, targetSpec, target, prerequisiteSpec, prerequisites);
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
				
				if (prerequisiteSpec.getBuildSpec() != null) {
					
					final TargetSpec<CONTEXT, PREREQUISITE> subTargetSpec = prerequisiteSpec.getBuildSpec().getSubTarget();
					
					findTargets(
					        config.addIndent(),
					        subTargetSpec,
							prerequisite,
							subTarget -> {
	
						final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(config.logContext, prerequisite, subTarget);
						
						prerequisiteSet.add(subPrerequisite);
						
						if (prerequisiteSet.size() == subSet.size()) {
							listener.accept(prerequisiteSet);
						}
					});
				}
				else {
				    
					final Prerequisite<PREREQUISITE> subPrerequisite;
					
					if (prerequisite instanceof File) {

					    // Only prerequisite, no action to build target so probably a source file
					    subPrerequisite = new Prerequisite<PREREQUISITE>(config.logContext, prerequisite, (File)prerequisite);
					}
					else {
					    subPrerequisite = new Prerequisite<>(
                                                config.logContext,
                                                prerequisite,
                                                new UnknownTarget<>(config.logContext, prerequisite));
					}
					
					prerequisiteSet.add(subPrerequisite);
	
					if (prerequisiteSet.size() == subSet.size()) {
						listener.accept(prerequisiteSet);
					}
				}
			}
		}
	}
}
