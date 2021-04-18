package org.jutils.concurrency.dependencyresolution.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.executor.Action;
import org.jutils.concurrency.dependencyresolution.executor.ActionWithResult;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public abstract class TargetSpec<CONTEXT extends TaskContext, TARGET> {

	private final Class<TARGET> type;
    private final String semanticType;
    private final String semanticAction;

	private final Function<TARGET, String> description;
	
	private final List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites;

	private final Constraint constraint;
	private final ActionFunction<CONTEXT, TARGET> actionFunction;
	
	private ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult;
	private ProcessResult<CONTEXT, TARGET, ?> onResult;

	abstract TargetDefinition<TARGET> createTargetDefinition(LogContext logContext, CONTEXT context, TARGET target, List<Prerequisites> prerequisitesList);

	public abstract TargetSpec<CONTEXT, TARGET> addPrerequisiteSpecs(List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites);
	
	TargetSpec(
			Class<TARGET> type,
            String semanticType,
            String semanticAction,
			Function<TARGET, String> description,
			List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites,
			Constraint constraint,
			ActionFunction<CONTEXT, TARGET> actionFunction,
			ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult,
			ProcessResult<CONTEXT, TARGET, ?> onResult) {

		this.type = type;

		this.semanticType = semanticType;
		this.semanticAction = semanticAction;
		
		this.description = description;
		this.prerequisites = prerequisites != null ? Collections.unmodifiableList(prerequisites) : null;
		
		this.constraint = constraint;
		this.actionFunction = actionFunction;

		this.actionWithResult = actionWithResult;
		this.onResult = onResult;
	}

	final String getSemanticType() {
        return semanticType;
    }

    final String getSemanticAction() {
        return semanticAction;
    }

    final String getDescription(TARGET target) {
		return description.apply(target);
	}
	
	final Function<TARGET, String> getDescriptionFunction() {
		return description;
	}

	final boolean hasAction() {
		return actionFunction != null || actionWithResult != null;
	}
	
	TargetSpec(TargetSpec<CONTEXT, TARGET> other, List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {
		
		this(
				other.type,
				other.semanticType,
				other.semanticAction,
				other.description,
				merge(other.prerequisites, additionalPrerequisites),
				other.constraint,
				other.actionFunction,
				other.actionWithResult,
				other.onResult);
	}

	private static <T> List<T> merge(List<T> list1, List<T> list2) {
	
		final List<T> list;
		
		if (list1 == null) {
			list = list2;
		}
		else if (list2 == null) {
			list = list1;
		}
		else {
			list = new ArrayList<>(list1.size() + list2.size());
		
			list.addAll(list1);
			list.addAll(list2);
		}
		
		return list;
	}
	
	public final Class<TARGET> getType() {
		return type;
	}

	List<PrerequisiteSpec<CONTEXT, TARGET, ?>> getPrerequisiteSpecs() {
		return prerequisites;
	}

	Action<TARGET> makeAction() {
		return actionFunction != null
				? new Action<>(constraint, actionFunction)
				: null;
	}

	ActionWithResult<TARGET> makeActionWithResult() {
		return actionWithResult != null
				? new ActionWithResult<>(constraint, actionWithResult, onResult)
				: null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		final TargetSpec<?, ?> other = (TargetSpec<?, ?>) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
