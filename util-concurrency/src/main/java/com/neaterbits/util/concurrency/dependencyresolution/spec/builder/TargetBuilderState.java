package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.spec.FileTargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.InfoTargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.PrerequisiteSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetBuilderState<CONTEXT extends TaskContext, TARGET, FILE_TARGET> {

	private final Class<TARGET> targetType;
	
	private final String targetName;

	private final BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget;
	private final Class<FILE_TARGET> fileTargetType;
	private final Function<FILE_TARGET, File> file;
	private final Function<TARGET, String> qualifierName;
	private final Function<TARGET, String> description;
	
	private final List<PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?>> prerequisites;
	
	private Constraint constraint;
	
	private ActionFunction<CONTEXT, TARGET> actionFunction;

	private ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult;
	private ProcessResult<CONTEXT, TARGET, ?> onResult;
	
	TargetBuilderState(Class<TARGET> type, String targetName, Function<TARGET, String> qualifierName, Function<TARGET, String> description) {
		this.targetType = type;
		
		this.targetName = targetName;
		this.qualifierName = qualifierName;
		
		this.getFileTarget = null;
		this.fileTargetType = null;
		this.file = null;
		
		this.description = description;
		this.prerequisites = new ArrayList<>();
	}
	
	TargetBuilderState(Class<TARGET> type, Class<FILE_TARGET> fileTargetType, BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget, Function<FILE_TARGET, File> file, Function<TARGET, String> description) {
		this.targetType = type;
		
		this.targetName = null;
		this.qualifierName = null;
		
		this.getFileTarget = getFileTarget;
		this.fileTargetType = fileTargetType;
		this.file = file;
		
		this.description = description;
		this.prerequisites = new ArrayList<>();
	}
	
	final void addPrerequisiteBuilder(PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> builder) {
		
		Objects.requireNonNull(builder);

		prerequisites.add(builder);
	}
	
	final List<PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?>> getPrerequisites() {
		return prerequisites;
	}
	
	final void setAction(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction) {
		
		Objects.requireNonNull(constraint);
		Objects.requireNonNull(actionFunction);

		this.constraint = constraint;
		this.actionFunction = actionFunction;
	}

	final void setActionWithResult(Constraint constraint, ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult) {
		
		Objects.requireNonNull(constraint);
		Objects.requireNonNull(actionWithResult);

		this.constraint = constraint;
		this.actionWithResult = actionWithResult;
	}
	
	final void setOnResult(ProcessResult<CONTEXT, TARGET, ?> onResult) {
		Objects.requireNonNull(onResult);
	
		this.onResult = onResult;
	}
	

	final TargetSpec<CONTEXT, TARGET> build() {
		
		final List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites = new ArrayList<>(this.prerequisites.size());
		
		for (PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisite : this.prerequisites) {
			prerequisites.add(prerequisite.build());
		}
		
		return targetName != null
				? new InfoTargetSpec<>(
						targetType,
						targetName,
						qualifierName,
						description,
						prerequisites,
						constraint,
						actionFunction,
						actionWithResult,
						onResult)
				: new FileTargetSpec<>(
						
						targetType,
						fileTargetType,
						getFileTarget,
						file,
						description,
						prerequisites,
						constraint,
						actionFunction,
						actionWithResult,
						onResult);
	}
}
