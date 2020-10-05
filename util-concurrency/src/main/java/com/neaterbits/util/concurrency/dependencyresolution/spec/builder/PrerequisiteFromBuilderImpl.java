package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

class PrerequisiteFromBuilderImpl<CONTEXT extends TaskContext, TARGET, PREREQUISITE> 
		implements PrerequisiteFromBuilder<CONTEXT, PREREQUISITE> {

	private final String description;
	private final PrerequisiteBuilderState<CONTEXT, TARGET, Void, Void> prerequisiteBuilderState;
	private Function<TARGET, PREREQUISITE> from;
	private Function<PREREQUISITE, File> withFile;
	
	PrerequisiteFromBuilderImpl(
			String description,
			Function<TARGET, PREREQUISITE> from,
			PrerequisiteBuilderState<CONTEXT, TARGET, Void, Void> prerequisiteBuilderState) {
		
		Objects.requireNonNull(description);
		Objects.requireNonNull(from);
		Objects.requireNonNull(prerequisiteBuilderState);
		
		this.description = description;
		this.from = from;
		this.prerequisiteBuilderState = prerequisiteBuilderState;
	}

	@Override
	public void withFile(Function<PREREQUISITE, File> withFile) {

		if (this.withFile != null) {
			throw new IllegalStateException();
		}

		this.withFile = withFile;
		
		prerequisiteBuilderState.setSingleFile(from, withFile);
	}

	final String getDescription() {
		return description;
	}
}
