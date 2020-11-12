package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisiteActionOrTargetActionBuilderImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, PREREQUISITE> 
        extends PrerequisiteActionBuilderImpl<CONTEXT, TARGET, FILE_TARGET, PREREQUISITE>
        implements PrerequisiteActionOrTargetActionBuilder<CONTEXT, TARGET, PREREQUISITE> {

    
    PrerequisiteActionOrTargetActionBuilderImpl(
            TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState,
            PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisiteBuilderState) {

        super(targetBuilderState, prerequisiteBuilderState);
    }

    @Override
    public void action(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction) {
        
        getTargetBuilderState().setAction(constraint, actionFunction);

    }
}
