package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class RecursiveBuildInfo<CONTEXT extends TaskContext, TARGET, PREREQUISITE>
    extends RecursiveBuildSpec<CONTEXT, TARGET, PREREQUISITE> {
    
    @FunctionalInterface
    public interface CreateTargetDefinition<CONTEXT extends TaskContext, TARGET> {
        
        TargetDefinition<TARGET> create(
                LogContext logContext,
                CONTEXT context,
                TARGET target,
                List<Prerequisites> prerequisitesList);
    }
	
	private final CreateTargetDefinition<CONTEXT, PREREQUISITE> createSubTarget;

    public RecursiveBuildInfo(
            RecursiveBuildSpec<CONTEXT, TARGET, PREREQUISITE> spec,
            CreateTargetDefinition<CONTEXT, PREREQUISITE> createSubTarget) {

        this(
                spec.getSubPrerequisitesFunction(),
                spec.getTargetFromPrerequisiteFunction(),
                createSubTarget);
    }

    public RecursiveBuildInfo(
			BiFunction<CONTEXT, TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, TARGET> getDependencyFromPrerequisite,
			CreateTargetDefinition<CONTEXT, PREREQUISITE> createSubTarget) {

	    super(getSubPrerequisites, getDependencyFromPrerequisite);
	    
		Objects.requireNonNull(createSubTarget);
		
		this.createSubTarget = createSubTarget;
	}

    TargetDefinition<PREREQUISITE> createTarget(
            LogContext logContext,
            CONTEXT context,
            PREREQUISITE target,
            List<Prerequisites> prerequisitesList) {
    
        return createSubTarget.create(logContext, context, target, prerequisitesList);
    }
}
