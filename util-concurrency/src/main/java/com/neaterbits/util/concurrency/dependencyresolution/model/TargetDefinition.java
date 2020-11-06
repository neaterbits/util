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

	private final TargetReference<TARGET> targetReference;
	
	private List<Prerequisites> prerequisites;
	private final Action<TARGET> action;
	private final ActionWithResult<TARGET> actionWithResult;

	public abstract String targetSimpleLogString();
	public abstract String targetToLogString();

	public abstract <CONTEXT> TargetDefinition<TARGET> createTarget(
			LogContext logContext,
			CONTEXT context,
			TARGET target,
			List<Prerequisites> prerequisitesList);

	TargetDefinition(
			LogContext logContext,
			String logIdentifier,
			String logLocalIdentifier,
			TargetReference<TARGET> targetReference,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {
		
		if (
				(prerequisites == null || prerequisites.isEmpty())
			&& action == null
			&& actionWithResult == null
				
				) {

			throw new IllegalArgumentException("No action or prerequisites for target " + targetReference);
		}
		
		
		this.constructorLogSequenceNo = logConstructor(
				logContext,
				this,
				TargetDefinition.class,
				logIdentifier,
				logLocalIdentifier,
				targetReference.getDescription());
		
		this.logContext = logContext;
		this.targetReference = targetReference;
		this.prerequisites = logConstructorListField(logContext, LOG_FIELD_PREREQUISITES, prerequisites);
		this.action = action;
		this.actionWithResult = actionWithResult;
		
		updatePrerequisites(prerequisites);
		
		targetReference.setTargetDefinition(this);
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
		return targetReference.getType();
	}

	@Override
	public final String getDescription() {
		return targetReference.getDescription();
	}

	public final Function<TARGET, String> getDescriptionFunction() {
		return targetReference.getDescriptionFunction();
	}
	
	public final boolean isRoot() {
		return targetReference.isRoot();
	}
	
	public final TARGET getTargetObject() {
		return targetReference.getTargetObject();
	}
	
	public final TargetKey<TARGET> getTargetKey() {
		return targetReference;
	}
	
	public final TargetReference<TARGET> getTargetReference() {
		return targetReference;
	}

	public final Action<TARGET> getAction() {
		return action;
	}

	public final ActionWithResult<TARGET> getActionWithResult() {
		return actionWithResult;
	}
	
	public final Prerequisite<?> getFromPrerequisite() {
		return targetReference.getFromPrerequisite();
	}
	
	final void setFromPrerequisite(Prerequisite<?> prerequisite) {
		targetReference.setFromPrerequisite(prerequisite);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetReference == null) ? 0 : targetReference.hashCode());
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
		if (targetReference == null) {
			if (other.targetReference != null)
				return false;
		} else if (!targetReference.equals(other.targetReference))
			return false;
		return true;
	}
}
