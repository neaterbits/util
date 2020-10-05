package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class RecursiveBuildInfo<CONTEXT extends TaskContext, TARGET, PREREQUISITE> {

	private final BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getSubPrerequisites;
	private final Function<PREREQUISITE, TARGET> getDependencyFromPrerequisite;

	public RecursiveBuildInfo(
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, TARGET> getDependencyFromPrerequisite) {
		
		Objects.requireNonNull(getSubPrerequisites);
		Objects.requireNonNull(getDependencyFromPrerequisite);
		
		this.getSubPrerequisites = getSubPrerequisites;
		this.getDependencyFromPrerequisite = getDependencyFromPrerequisite;
	}

	public BiFunction<CONTEXT, ?, Collection<PREREQUISITE>> getSubPrerequisitesFunction() {
		return getSubPrerequisites;
	}
	
	public Function<PREREQUISITE, TARGET> getTargetFromPrerequisiteFunction() {
		return getDependencyFromPrerequisite;
	}
}
