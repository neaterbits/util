package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.InfoTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionFunction;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class InfoTargetSpec<CONTEXT extends TaskContext, TARGET> extends TargetSpec<CONTEXT, TARGET>{
	
	private final String name;
	private final Function<TARGET, String> qualifierName;

	public InfoTargetSpec(
			Class<TARGET> type,
			String name,
			Function<TARGET, String> qualifierName,
			Function<TARGET, String> description,
			List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites,
			Constraint constraint,
			ActionFunction<CONTEXT, TARGET> actionFunction,
			ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult,
			ProcessResult<CONTEXT, TARGET, ?> onResult) {
		
		super(type, description, prerequisites, constraint, actionFunction, actionWithResult, onResult);

		if (type == null && name == null) {
			throw new IllegalArgumentException("type == null && name == null");
		}

		this.name = name;
		this.qualifierName = qualifierName;
		
	}

	private InfoTargetSpec(InfoTargetSpec<CONTEXT, TARGET> other, List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

		super(other, additionalPrerequisites);
	
		this.name = other.name;
		this.qualifierName = other.qualifierName;
	}
	
	
	@Override
	public TargetSpec<CONTEXT, TARGET> addPrerequisiteSpecs(List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

		return new InfoTargetSpec<>(this, additionalPrerequisites);
	}
	
	String getName() {
		return name;
	}

	
	@Override
	TargetDefinition<TARGET> createTargetDefinition(LogContext logContext, CONTEXT context, TARGET target, List<Prerequisites> prerequisitesList) {
		
		return new InfoTarget<>(
				logContext,
				getType(),
				name,
				qualifierName,
				getDescriptionFunction().apply(target),
				target,
				prerequisitesList,
				makeAction(),
				makeActionWithResult());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InfoTargetSpec<?, ?> other = (InfoTargetSpec<?, ?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
