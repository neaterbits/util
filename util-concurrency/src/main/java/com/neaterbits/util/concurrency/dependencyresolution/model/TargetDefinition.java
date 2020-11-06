package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;
import com.neaterbits.util.concurrency.dependencyresolution.executor.BuildEntity;

public abstract class TargetDefinition<TARGET> extends BuildEntity implements Loggable {

	private static final String LOG_FIELD_PREREQUISITES = "prerequisites";

	private final LogContext logContext;
	private final int constructorLogSequenceNo;

	private final TargetKey<TARGET> targetKey;
	private final Function<TARGET, String> description;

	private List<Prerequisites> prerequisites;
	private final Action<TARGET> action;
	private final ActionWithResult<TARGET> actionWithResult;

	public abstract String targetSimpleLogString();
	public abstract String targetToLogString();

	TargetDefinition(
			LogContext logContext,
			String logIdentifier,
			String logLocalIdentifier,
            TargetKey<TARGET> targetKey,
			Function<TARGET, String> description,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {
		
		if (
				(prerequisites == null || prerequisites.isEmpty())
			&& action == null
			&& actionWithResult == null
				
				) {

			throw new IllegalArgumentException("No action or prerequisites for target " + targetToLogString());
		}
		
		
		this.constructorLogSequenceNo = logConstructor(
				logContext,
				this,
				TargetDefinition.class,
				logIdentifier,
				logLocalIdentifier,
				description.apply(targetKey.getTargetObject()));
		
		this.logContext = logContext;
        this.targetKey = targetKey;
		this.description = description;
		this.prerequisites = logConstructorListField(logContext, LOG_FIELD_PREREQUISITES, prerequisites);
		this.action = action;
		this.actionWithResult = actionWithResult;
		
		updatePrerequisites(prerequisites);
	}

	@Override
	public final int getConstructorLogSequenceNo() {
		return constructorLogSequenceNo;
	}

	public final List<Prerequisites> getPrerequisites() {
		return prerequisites;
	}
	
	public void updatePrerequisites(List<Prerequisites> prerequisites) {

		this.prerequisites = Collections.unmodifiableList(prerequisites);
	}

	public final LogContext getLogContext() {
		return logContext;
	}
	
	final Class<TARGET> getType() {
		return targetKey.getType();
	}

	@Override
	public final String getDescription() {
		return description.apply(targetKey.getTargetObject());
	}

	public final Function<TARGET, String> getDescriptionFunction() {
		return description;
	}
	
	public final TARGET getTargetObject() {
		return targetKey.getTargetObject();
	}
	
	public final TargetKey<TARGET> getTargetKey() {
		return targetKey;
	}
	
	public final Action<TARGET> getAction() {
		return action;
	}

	public final ActionWithResult<TARGET> getActionWithResult() {
		return actionWithResult;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetKey == null) ? 0 : targetKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TargetDefinition<?> other = (TargetDefinition<?>) obj;
		if (targetKey == null) {
			if (other.targetKey != null)
				return false;
		} else if (!targetKey.equals(other.targetKey))
			return false;
		return true;
	}
}
