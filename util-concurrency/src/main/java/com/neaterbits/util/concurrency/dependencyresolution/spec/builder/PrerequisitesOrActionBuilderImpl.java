package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.spec.PrerequisitesBuilderSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisitesOrActionBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
		extends PrerequisitesBuilderImpl<CONTEXT, TARGET, FILE_TARGET>
		implements PrerequisitesOrActionBuilder<CONTEXT, TARGET> {

	PrerequisitesOrActionBuilderImpl(Class<TARGET> type, String targetName, Function<TARGET, String> qualifierName, Function<TARGET, String> description) {
		super(type, targetName, qualifierName, description);
	}

	PrerequisitesOrActionBuilderImpl(
			Class<TARGET> type,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
			Function<FILE_TARGET, File> file,
			Function<TARGET, String> description) {

		super(type, fileTargetType, getFileTarget, file, description);
	}

	PrerequisitesOrActionBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState) {
		super(targetBuilderState);
	}

	@Override
	public PrerequisitesOrActionBuilder<CONTEXT, TARGET> withPrerequisites(PrerequisitesBuilderSpec<CONTEXT, TARGET> buildSpec) {

		final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState = getTargetBuilderState();
		
		final PrerequisitesBuilderImpl<CONTEXT, TARGET, ?> builder = new PrerequisitesBuilderImpl<>(targetBuilderState);
		
		buildSpec.buildSpec(builder);
		
		return this;
	}

	@Override
	public <R> ResultProcessor<CONTEXT, TARGET, R> actionWithResult(
			Constraint constraint,
			ActionWithResultFunction<CONTEXT, TARGET, R> function) {

		getTargetBuilderState().setActionWithResult(constraint, function);
		
		return new ResultProcessorImpl<>(getTargetBuilderState());
	}

	@Override
	public void action(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction) {

		getTargetBuilderState().setAction(constraint, actionFunction);
		
	}
}
