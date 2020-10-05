package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class NoTargetTargetBuildSpecPrerequisitesBuilderImpl<CONTEXT extends TaskContext>
	implements NoTargetTargetBuildSpecPrerequisitesBuilder {

	private final List<TargetSpec<CONTEXT, ?>> targetSpecs;

	private List<String> namedPrerequisites;
	
	NoTargetTargetBuildSpecPrerequisitesBuilderImpl(List<TargetSpec<CONTEXT, ?>> targetSpecs) {
		
		Objects.requireNonNull(targetSpecs);
		
		this.targetSpecs = targetSpecs;
	}

	@Override
	public void withNamedPrerequisites(String... prerequisites) {

		if (namedPrerequisites != null) {
			throw new IllegalStateException();
		}
		
		if (prerequisites.length == 0) {
			throw new IllegalArgumentException();
		}
		
		this.namedPrerequisites = new ArrayList<>(prerequisites.length);
		
		for (String prerequisite : prerequisites) {
			if (prerequisite == null) {
				throw new IllegalArgumentException();
			}
			
			if (prerequisite.isEmpty()) {
				throw new IllegalArgumentException();
			}
			
			namedPrerequisites.add(prerequisite);
		}
	}

	List<String> getNamedPrerequisites() {
		return namedPrerequisites;
	}

	List<TargetSpec<CONTEXT, ?>> getTargetSpecs() {
		return targetSpecs;
	}
}
