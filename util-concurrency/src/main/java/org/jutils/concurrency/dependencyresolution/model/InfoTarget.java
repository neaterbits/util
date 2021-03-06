package org.jutils.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jutils.concurrency.dependencyresolution.executor.Action;
import org.jutils.concurrency.dependencyresolution.executor.ActionWithResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class InfoTarget<TARGET> extends TargetDefinition<TARGET> {

    private final String identifier;
    
	public InfoTarget(
			LogContext logContext,
			Class<TARGET> type,
			String semanticType,
			String identifier,
			String semanticAction,
			String description,
			TARGET targetObject,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {

		super(
				logContext,
				new TargetKey<>(type, targetObject),
				prerequisites,
				action,
				actionWithResult,
				new TargetDebug(
				        semanticType,
		                identifier,
		                identifier,
		                semanticAction,
		                description,
		                true));
		
		Objects.requireNonNull(identifier);
	
		this.identifier = identifier;
	}
	
	@Override
    protected <CONTEXT extends TaskContext>
	boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites) {
        return false;
    }

	@Override
    protected String getTypeString() {
        return "info";
    }

	@Override
	public String toString() {
		return "InfoTarget [identifier=" + identifier + ", description=" + getDescription() + "]";
	}
}
