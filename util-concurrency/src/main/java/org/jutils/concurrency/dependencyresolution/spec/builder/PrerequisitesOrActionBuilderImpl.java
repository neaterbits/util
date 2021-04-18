package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.UpToDate;
import org.jutils.concurrency.dependencyresolution.spec.PrerequisitesBuilderSpec;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;

final class PrerequisitesOrActionBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
		extends PrerequisitesBuilderImpl<CONTEXT, TARGET, FILE_TARGET>
		implements PrerequisitesOrActionBuilder<CONTEXT, TARGET> {

    PrerequisitesOrActionBuilderImpl(String name, String semanticType, String semanticAction, String description) {
        super(name, semanticType, semanticAction, description);
    }
	
    PrerequisitesOrActionBuilderImpl(
            Class<TARGET> type,
            String semanticType,
            String semanticAction,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {
        
		super(type, semanticType, semanticAction, getIdentifier, getDescription);
	}

	PrerequisitesOrActionBuilderImpl(
			Class<TARGET> type,
			String semanticType,
			String semanticAction,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
			Function<FILE_TARGET, File> file,
			Function<TARGET, String> getDescription) {

		super(type, semanticType, semanticAction, fileTargetType, getFileTarget, file, getDescription);
	}
	
	PrerequisitesOrActionBuilderImpl(
	        Class<TARGET> type,
	        String semanticType,
	        String semanticAction,
            UpToDate<CONTEXT, TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {

	    super(type, semanticType, semanticAction, upToDate, getIdentifier, getDescription);
	}

	PrerequisitesOrActionBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState) {
		super(targetBuilderState);
	}

	@Override
    public PrerequisitesOrActionBuilder<CONTEXT, TARGET> withNamedPrerequisite(String name) {

	    final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState = getTargetBuilderState();

	    final PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisite
	        = new PrerequisiteBuilderState<>(name);
	    
	    targetBuilderState.addPrerequisiteBuilder(prerequisite);
	    
        return this;
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
