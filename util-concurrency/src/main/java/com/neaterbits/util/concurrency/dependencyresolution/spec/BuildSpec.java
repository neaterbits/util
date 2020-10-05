package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.Objects;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class BuildSpec<CONTEXT extends TaskContext, PREREQUISITE> {

	private final TargetSpec<CONTEXT, PREREQUISITE> subTarget;

	public BuildSpec(TargetSpec<CONTEXT, PREREQUISITE> subTarget) {
		
		Objects.requireNonNull(subTarget);
		
		this.subTarget = subTarget;
	}

	public TargetSpec<CONTEXT, PREREQUISITE> getSubTarget() {
		return subTarget;
	}
}
