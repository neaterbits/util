package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.function.Consumer;

import org.jutils.concurrency.dependencyresolution.spec.BuildSpec;
import org.jutils.concurrency.scheduling.task.TaskContext;

public class NoTargetPrerequisiteBuilderImpl<CONTEXT extends TaskContext, PREREQUISITE>
		implements NoTargetPrerequisiteBuilder<CONTEXT, PREREQUISITE> {

	private final PrerequisiteBuilderState<CONTEXT, Object, Void, Void> prerequisiteBuilderState;
	
	public NoTargetPrerequisiteBuilderImpl(
			PrerequisiteBuilderState<CONTEXT, Object, Void, Void> prerequisiteBuilderState) {
		this.prerequisiteBuilderState = prerequisiteBuilderState;
	}

	@Override
	public void buildBy(Consumer<TypedSubTargetBuilder<CONTEXT, PREREQUISITE>> prerequisiteTargets) {

		final TypedSubTargetBuilderImpl<CONTEXT, PREREQUISITE> sub = new TypedSubTargetBuilderImpl<>(); 
		
		prerequisiteTargets.accept(sub);
		
		final TargetBuilderState<CONTEXT, PREREQUISITE, ?> subTargetBuilderState = sub.build();
		
		prerequisiteBuilderState.setBuild(new BuildSpec<>(subTargetBuilderState.build()));
	}
}
