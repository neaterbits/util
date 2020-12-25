package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;

public final class ActionWithResult<TARGET> {

	private final Constraint constraint;
	private final ActionWithResultFunction<?, TARGET, ?> actionWithResult;
	private final ProcessResult<?, TARGET, ?> onResult;

	public ActionWithResult(
			Constraint constraint,
			ActionWithResultFunction<?, TARGET, ?> actionWithResult,
			ProcessResult<?, TARGET, ?> onResult) {
	    
	    Objects.requireNonNull(constraint);
	    Objects.requireNonNull(actionWithResult);
	    
		this.constraint = constraint;
		this.actionWithResult = actionWithResult;
		this.onResult = onResult;
	}

	Constraint getConstraint() {
		return constraint;
	}

	ActionWithResultFunction<?, TARGET, ?> getActionWithResult() {
		return actionWithResult;
	}

	ProcessResult<?, TARGET, ?> getOnResult() {
		return onResult;
	}
}
