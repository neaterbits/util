package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.model.UpToDate;
import com.neaterbits.util.concurrency.dependencyresolution.spec.FileTargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.InfoTargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.NamedTargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.PrerequisiteSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.UpToDateTargetSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetBuilderState<CONTEXT extends TaskContext, TARGET, FILE_TARGET> {

	private final Class<TARGET> targetType;
	
	private final String semanticType;
	private final String semanticAction;

	private final String targetName;

	private final BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget;
	private final Class<FILE_TARGET> fileTargetType;
	private final Function<FILE_TARGET, File> file;
	private final Function<TARGET, String> getIdentifier;
	private final Function<TARGET, String> getDescription;
	
	private final UpToDate<CONTEXT, TARGET> upToDate;
	
	private final List<PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?>> prerequisites;
	
	private Constraint constraint;
	
	private ActionFunction<CONTEXT, TARGET> actionFunction;

	private ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult;
	private ProcessResult<CONTEXT, TARGET, ?> onResult;

	TargetBuilderState(
	        String targetName,
	        String semanticType,
	        String semanticAction,
	        String description) {
	    
	    this(targetName, semanticType, semanticAction, description, null);
	}
	
    TargetBuilderState(
            String targetName,
            String semanticType,
            String semanticAction,
            String description,
            List<PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?>> prerequisites) {

        this.targetType = null;

        this.semanticType = semanticType;
        this.semanticAction = semanticAction;

        this.targetName = targetName;
        this.getIdentifier = null;

        this.getFileTarget = null;
        this.fileTargetType = null;
        this.file = null;

        this.upToDate = null;
        
        this.getDescription = target -> description;
        this.prerequisites = prerequisites != null
                ? new ArrayList<>(prerequisites)
                : new ArrayList<>();
    }

	TargetBuilderState(
	        Class<TARGET> type,
	        String semanticType,
	        String semanticAction,
	        Function<TARGET, String> getIdentifier,
	        Function<TARGET, String> getDescription) {

	    this.targetType = type;
		
        this.semanticType = semanticType;
        this.semanticAction = semanticAction;

	    this.targetName = null;
		this.getIdentifier = getIdentifier;
		
		this.getFileTarget = null;
		this.fileTargetType = null;
		this.file = null;
		
		this.upToDate = null;
		
		this.getDescription = getDescription;
		this.prerequisites = new ArrayList<>();
	}
	
	TargetBuilderState(
	        Class<TARGET> type,
	        String semanticType,
	        String semanticAction,
	        Class<FILE_TARGET> fileTargetType,
	        BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
	        Function<FILE_TARGET, File> file,
	        Function<TARGET, String> description) {

	    this.targetType = type;

        this.semanticType = semanticType;
        this.semanticAction = semanticAction;
	    
		this.targetName = null;
		this.getIdentifier = null;
		
		this.getFileTarget = getFileTarget;
		this.fileTargetType = fileTargetType;
		this.file = file;
		
		this.upToDate = null;
		
		this.getDescription = description;
		this.prerequisites = new ArrayList<>();
	}
	
	TargetBuilderState(
	        Class<TARGET> type, String semanticType, String semanticAction,
            UpToDate<CONTEXT, TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription) {

	    Objects.requireNonNull(type);
	    Objects.requireNonNull(upToDate);
	    Objects.requireNonNull(getIdentifier);
	    Objects.requireNonNull(getDescription);
	    
	    this.targetType = type;
	    this.targetName = null;
	    
	    this.semanticType = semanticType;
	    this.semanticAction = semanticAction;

	    this.getIdentifier = getIdentifier;
	    
	    this.getFileTarget = null;
	    this.fileTargetType = null;
	    this.file = null;

	    this.upToDate = upToDate;
	    
	    this.getDescription = getDescription;
	    this.prerequisites = new ArrayList<>();
	}
	
	final void addPrerequisiteBuilder(PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> builder) {
		
		Objects.requireNonNull(builder);

		prerequisites.add(builder);
	}
	
	final List<PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?>> getPrerequisites() {
		return prerequisites;
	}
	
	final void setAction(Constraint constraint, ActionFunction<CONTEXT, TARGET> actionFunction) {
		
		Objects.requireNonNull(constraint);
		Objects.requireNonNull(actionFunction);

		this.constraint = constraint;
		this.actionFunction = actionFunction;
	}

	final void setActionWithResult(Constraint constraint, ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult) {
		
		Objects.requireNonNull(constraint);
		Objects.requireNonNull(actionWithResult);

		this.constraint = constraint;
		this.actionWithResult = actionWithResult;
	}
	
	final void setOnResult(ProcessResult<CONTEXT, TARGET, ?> onResult) {
		Objects.requireNonNull(onResult);
	
		this.onResult = onResult;
	}
	

	final TargetSpec<CONTEXT, TARGET> build() {
		
		final List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites = new ArrayList<>(this.prerequisites.size());
		
		for (PrerequisiteBuilderState<CONTEXT, TARGET, ?, ?> prerequisite : this.prerequisites) {
			prerequisites.add(prerequisite.build());
		}
		
		final TargetSpec<CONTEXT, TARGET> targetSpec;
		
		if (targetName != null) {
		    
		    @SuppressWarnings({ "unchecked", "rawtypes" })
            final TargetSpec<CONTEXT, TARGET> spec
		        = (TargetSpec)new NamedTargetSpec<CONTEXT>(
		                targetName,
		                semanticType,
		                semanticAction,
		                getDescription.apply(null),
		                (List)prerequisites,
		                constraint,
		                (ActionFunction)actionFunction,
		                (ActionWithResultFunction)actionWithResult,
		                (ProcessResult)onResult);

		    targetSpec = spec;
		}
		else if (fileTargetType != null && getFileTarget != null && file != null) {

		    targetSpec = new FileTargetSpec<>(
                    
                    targetType,
                    semanticType,
                    semanticAction,
                    fileTargetType,
                    getFileTarget,
                    file,
                    getDescription,
                    prerequisites,
                    constraint,
                    actionFunction,
                    actionWithResult,
                    onResult);
		}
		else if (upToDate != null) {
		    
		    targetSpec = new UpToDateTargetSpec<>(
		            targetType,
                    semanticType,
                    semanticAction,
		            upToDate,
		            getIdentifier,
		            getDescription,
		            prerequisites,
		            constraint,
		            actionFunction,
		            actionWithResult,
		            onResult);
		}
		else if (targetType != null) {
		
		    targetSpec = new InfoTargetSpec<>(
						targetType,
	                    semanticType,
	                    semanticAction,
						getIdentifier,
						getDescription,
						prerequisites,
						constraint,
						actionFunction,
						actionWithResult,
						onResult);
		}
		else {
		    throw new IllegalStateException();
		}

		return targetSpec;
	}
}
