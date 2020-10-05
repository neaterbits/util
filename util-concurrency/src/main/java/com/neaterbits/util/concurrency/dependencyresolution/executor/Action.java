package com.neaterbits.util.concurrency.dependencyresolution.executor;


import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;

public final class Action<TARGET> {

	private final Constraint constraint;
	private final ActionFunction<?, TARGET> actionFunction;

	public Action(Constraint constraint, ActionFunction<?, TARGET> actionFunction) {
		this.constraint = constraint;
		this.actionFunction = actionFunction;
	}

	Constraint getConstraint() {
		return constraint;
	}

	ActionFunction<?, TARGET> getActionFunction() {
		return actionFunction;
	}

	@Override
	public String toString() {
		return "Action [constraint=" + constraint + ", actionFunction=" + actionFunction + "]";
	}
}
