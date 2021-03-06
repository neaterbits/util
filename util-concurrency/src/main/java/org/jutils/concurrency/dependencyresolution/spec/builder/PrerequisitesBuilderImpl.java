package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.UpToDate;
import org.jutils.concurrency.scheduling.task.TaskContext;

class PrerequisitesBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
		implements PrerequisitesBuilder<CONTEXT, TARGET> {
	
	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	
    PrerequisitesBuilderImpl(String name, String semanticType, String semanticAction, String description) {

        this.targetBuilderState = new TargetBuilderState<>(name, semanticType, semanticAction, description);
    }
	
	PrerequisitesBuilderImpl(Class<TARGET> type, String semanticType, String semanticAction, Function<TARGET, String> getIdentifier, Function<TARGET, String> getDescription) {

		this.targetBuilderState = new TargetBuilderState<>(type, semanticType, semanticAction, getIdentifier, getDescription);
	}

	PrerequisitesBuilderImpl(
	        Class<TARGET> type,
	        String semanticType,
	        String semanticAction,
	        Class<FILE_TARGET> fileTargetType,
	        BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
	        Function<FILE_TARGET, File> file,
	        Function<TARGET, String> getDescription) {

		this.targetBuilderState = new TargetBuilderState<>(
		                                    type,
		                                    semanticType,
		                                    semanticAction,
		                                    fileTargetType,
		                                    getFileTarget,
		                                    file,
		                                    getDescription);
	}
	
	PrerequisitesBuilderImpl(
            Class<TARGET> type,
            String semanticType,
            String semanticAction,
            UpToDate<CONTEXT, TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {

        this.targetBuilderState = new TargetBuilderState<>(
                                            type,
                                            semanticType,
                                            semanticAction,
                                            upToDate,
                                            getIdentifier,
                                            getDescription);
    }
	
	
	PrerequisitesBuilderImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState) {

		Objects.requireNonNull(targetBuilderState);
		
		this.targetBuilderState = targetBuilderState;
	}

	@Override
	public final TargetPrerequisitesBuilder<CONTEXT, TARGET> withPrerequisites(String description) {
		return new TargetPrerequisitesBuilderImpl<>(targetBuilderState, description);
	}

	@Override
	public final TargetPrerequisiteBuilder<CONTEXT, TARGET> withPrerequisite(String description) {
		return new TargetPrerequisiteBuilderImpl<>(targetBuilderState, description);
	}

	final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> getTargetBuilderState() {
		return targetBuilderState;
	}

	final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> build() {
		return targetBuilderState;
	}
}
