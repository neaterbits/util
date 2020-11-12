package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;

public final class InfoTarget<TARGET> extends TargetDefinition<TARGET> {

    private final String identifier;
    
	public InfoTarget(
			LogContext logContext,
			Class<TARGET> type,
			String identifier,
			String description,
			TARGET targetObject,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {

		super(
				logContext,
				identifier,
				identifier,
				new TargetKey<>(type, targetObject),
				description,
				prerequisites,
				action,
				actionWithResult);
		
		Objects.requireNonNull(identifier);
	
		this.identifier = identifier;
	}
	
	@Override
	public String getDebugString() {
		return getLogIdentifier();
	}

	@Override
	public String toString() {
		return "InfoTarget [identifier=" + identifier + ", description=" + getDescription() + "]";
	}
}
