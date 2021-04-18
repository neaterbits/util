package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.executor.SubPrerequisites;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetIteratingBuilder<CONTEXT extends TaskContext, TARGET> {

	<PREREQUISITE>
	PrerequisiteActionOrTargetActionBuilder<CONTEXT, TARGET, PREREQUISITE>
	    fromIterating(
	            Constraint constraint,
	            BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites);

	<PREREQUISITE, SUB_TARGET>
	PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE> fromIteratingAndBuildingRecursively(
			Constraint constraint,
			Class<SUB_TARGET> subTargetType,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			BiFunction<CONTEXT, SUB_TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, SUB_TARGET> getDependencyFromPrerequisite);
}
