package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

abstract class PrerequisitesFinder {

	final AsyncExecutor asyncExecutor;
	
	PrerequisitesFinder(AsyncExecutor asyncExecutor) {
		
		Objects.requireNonNull(asyncExecutor);

		this.asyncExecutor = asyncExecutor;
	}

	abstract <CONTEXT extends TaskContext, TARGET>
	void findTargets(
			TargetSpec<CONTEXT, TARGET> targetSpec,
			LogContext logContext,
			CONTEXT context,
			TARGET target,
			TargetFinderLogger logger,
			int indent,
			Consumer<TargetDefinition<TARGET>> targetCreated);
	
	final <CONTEXT extends TaskContext, TARGET, PREREQUISITE>
	void getPrerequisites(
			LogContext logContext,
			CONTEXT context,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			TargetFinderLogger logger,
			int indent,
			Consumer<Set<Prerequisite<?>>> listener) {
		
		if (prerequisiteSpec.getConstraint() != null) {

			asyncExecutor.schedule(
					prerequisiteSpec.getConstraint(),
					null,
					param -> {
						return prerequisiteSpec.getPrerequisites(context, target);
					},
					(param, result) -> {

						getPrerequisites(logContext, context, targetSpec, target, prerequisiteSpec, result, logger, indent, prerequisites -> {
						
							if (logger != null) {
								logger.onPrerequisites(indent, targetSpec, target, prerequisiteSpec, prerequisites);
							}
														
							listener.accept(prerequisites);
						});
					});
		} else {
			final Collection<PREREQUISITE> sub = prerequisiteSpec.getPrerequisites(context, target);

			getPrerequisites(logContext, context, targetSpec, target, prerequisiteSpec, sub, logger, indent, prerequisites -> {
				
				if (logger != null) {
					logger.onPrerequisites(indent, targetSpec, target, prerequisiteSpec, prerequisites);
				}
				
				listener.accept(prerequisites);
			});
		}
	}

	private <CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE>
	void getPrerequisites(
			LogContext logContext,
			CONTEXT context,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			Collection<PREREQUISITE> sub,
			TargetFinderLogger logger,
			int indent,
			Consumer<Set<Prerequisite<?>>> listener) {

		if (sub == null) {
			throw new IllegalStateException("No prerequisites for " + targetSpec.getType().getSimpleName() + "/" + target + "/" + prerequisiteSpec.getDescription());
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
							subTargetSpec,
							logContext,
							context,
							prerequisite,
							logger, indent + 1,
							subTarget -> {
	
						final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(logContext, prerequisite, subTarget);
						
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
					
					final Prerequisite<PREREQUISITE> subPrerequisite = new Prerequisite<>(logContext, prerequisite, sourceFile);
					
					prerequisiteSet.add(subPrerequisite);
	
					if (prerequisiteSet.size() == sub.size()) {
						listener.accept(prerequisiteSet);
					}
				}
			}
		}
	}
}
