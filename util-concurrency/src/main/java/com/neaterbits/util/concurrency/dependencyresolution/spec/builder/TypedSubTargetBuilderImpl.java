package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.model.UpToDate;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TypedSubTargetBuilderImpl<CONTEXT extends TaskContext, TARGET>
		extends SubTargetBuilderImpl<CONTEXT, TARGET, PrerequisitesOrActionBuilder<CONTEXT,TARGET>>
		implements TypedSubTargetBuilder<CONTEXT, TARGET> {

	private PrerequisitesOrActionBuilderImpl<CONTEXT, TARGET, ?> builder;
	
	private <FILE_TARGET> PrerequisitesOrActionBuilder<CONTEXT, TARGET> set(PrerequisitesOrActionBuilderImpl<CONTEXT, TARGET, FILE_TARGET> builder) {
		
		Objects.requireNonNull(builder);
		
		if (this.builder != null) {
			throw new IllegalStateException();
		}

		this.builder = builder;
		
		return builder;
	}
	
	@Override
	public PrerequisitesOrActionBuilder<CONTEXT, TARGET> addNamedSubTarget(String name, String description) {
		return set(new PrerequisitesOrActionBuilderImpl<>(name, description));
	}

    @Override
    public PrerequisitesOrActionBuilder<CONTEXT, TARGET> addInfoSubTarget(
            Class<TARGET> type,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {
        
        return set(new PrerequisitesOrActionBuilderImpl<>(type, getIdentifier, getDescription));
    }

    @Override
	public PrerequisitesOrActionBuilder<CONTEXT, TARGET> addFileSubTarget(Class<TARGET> type, Function<TARGET, File> file,
			Function<TARGET, String> description) {

		return set(new PrerequisitesOrActionBuilderImpl<>(type, type, (context, target) -> target, file, description));
	}

	@Override
	public <FILE_TARGET> PrerequisitesOrActionBuilder<CONTEXT, TARGET> addFileSubTarget(
			Class<TARGET> type,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
			Function<FILE_TARGET, File> file,
			Function<TARGET, String> description) {
		
		return set(new PrerequisitesOrActionBuilderImpl<CONTEXT, TARGET, FILE_TARGET>(type, fileTargetType, getFileTarget, file, description));
	}

	@Override
    public PrerequisitesOrActionBuilder<CONTEXT, TARGET> addFilesSubTarget(
            Class<TARGET> type,
            UpToDate<TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {

        return set(new PrerequisitesOrActionBuilderImpl<>(type, upToDate, getIdentifier, getDescription));
    }

    TargetBuilderState<CONTEXT, TARGET, ?> build() {
		return builder.build();
	}
}
