package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.neaterbits.util.concurrency.dependencyresolution.spec.PrerequisiteSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetBuilderSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class TargetBuilderImpl<CONTEXT extends TaskContext> implements TargetBuilder<CONTEXT> {

	private final List<NoTargetPrerequisitesBuilderImpl<CONTEXT>> builders;

	private final List<NoTargetTargetBuildSpecPrerequisitesBuilderImpl<CONTEXT>> targetBuildSpecs;
	
	public TargetBuilderImpl() {
		this.builders = new ArrayList<>();
		this.targetBuildSpecs = new ArrayList<>();
	}
	
	@Override
	public NoTargetPrerequisitesBuilder<CONTEXT> addTarget(String targetName, String semanticType, String semanticAction, String description) {
		
		final NoTargetPrerequisitesBuilderImpl<CONTEXT> builder = new NoTargetPrerequisitesBuilderImpl<>(
		                                                                                targetName,
		                                                                                semanticType,
		                                                                                semanticAction,
		                                                                                description);

		builders.add(builder);
		
		return builder;
	}
	
	
	@Override
	public NoTargetTargetBuildSpecPrerequisitesBuilder addTarget(TargetBuilderSpec<CONTEXT> subTarget) {

		final NoTargetTargetBuildSpecPrerequisitesBuilderImpl<CONTEXT> builder
			= new NoTargetTargetBuildSpecPrerequisitesBuilderImpl<>(subTarget.buildTargetSpecs());
		
		targetBuildSpecs.add(builder);
		
		return builder;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<TargetSpec<CONTEXT, ?>> build() {
		
		final List<TargetSpec<CONTEXT, ?>> targets = new ArrayList<>();
		
		for (NoTargetTargetBuildSpecPrerequisitesBuilderImpl<CONTEXT> builder : targetBuildSpecs) {

			final List<String> namedPrerequisites = builder.getNamedPrerequisites();
			
			if (namedPrerequisites != null && !namedPrerequisites.isEmpty()) {
				
				final List<PrerequisiteSpec<TaskContext, Object, Object>> prerequisiteSpecs = namedPrerequisites.stream()
						.map(named -> new PrerequisiteSpec<>(named))
						.collect(Collectors.toList());


				builder.getTargetSpecs().forEach(targetSpec -> targets.add(targetSpec.addPrerequisiteSpecs((List)prerequisiteSpecs)));
			}
			else {
				builder.getTargetSpecs().forEach(targets::add);
			}
		}
		
		builders.stream()
				.map(builder -> builder.build())
				.forEach(targets::add);
		
		return targets;
	}
}
