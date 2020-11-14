package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;
import com.neaterbits.util.concurrency.statemachine.BaseState;

public abstract class BaseTargetState<CONTEXT extends TaskContext>
		extends BaseState<BaseTargetState<CONTEXT>>
		implements TargetOps<CONTEXT> {

	final TargetDefinition<?> target;
	final TargetExecutorLogger logger;

	abstract Status getStatus();
	
	Exception getException() {
		throw new IllegalStateException();
	}
	
	public BaseTargetState(TargetDefinition<?> target, TargetExecutorLogger logger) {
		Objects.requireNonNull(target);
		
		this.target = target;
		this.logger = logger;
	}
	
	@Override
	public BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context) {
		throw new IllegalStateException("not implemented in state " + getClass().getSimpleName());
	}

	@Override
	public BaseTargetState<CONTEXT> onActionPerformed(TargetExecutionContext<CONTEXT> context) {
		throw new IllegalStateException();
	}
	
	@Override
	public BaseTargetState<CONTEXT> onActionError(TargetExecutionContext<CONTEXT> context, Exception ex) {
		throw new IllegalStateException();
	}

	@Override
	public BaseTargetState<CONTEXT> onActionWithResultPerformed() {
		throw new IllegalStateException();
	}

	final BaseTargetState<CONTEXT> testForRecursiveTargets(TargetExecutionContext<CONTEXT> context) {

	    final boolean targetsAdded = addRecursiveBuildTargetsIfAny(context.state, context.taskContext, target, context.logger);
	        
        final BaseTargetState<CONTEXT> nextState;
        
        if (targetsAdded) {
            //context.state.moveTargetFromToScheduledToActionPerformedCollect(target);
            
            nextState = new TargetStateRecursiveTargets<>(target, logger);
        }
        else {
            onCompletedTarget(context, target, null, false);
            
            nextState = new TargetStateDone<>(target, logger);
        }
        
        return nextState;
	}
	
	final void onCompletedTarget(
			TargetExecutionContext<CONTEXT> context,
			TargetDefinition<?> target,
			Exception exception,
			boolean async) {

		
		if (context.logger != null) {
			context.logger.onTargetDone(target, exception, context.state);
		}
		
		// context.state.onCompletedTarget(target, exception);

		context.state.onCompletedTarget(target.getTargetKey());
	}

    private 
    boolean addRecursiveBuildTargetsIfAny(ExecutorState<CONTEXT> targetState, TaskContext context, TargetDefinition<?> target, TargetExecutorLogger logger) {

        final TargetPaths targetPaths = targetState.getPaths(target.getTargetKey());

        boolean anyAdded = false;
        
        for (TargetPath targetPath : targetPaths.getPaths()) {
        
            final TargetEdge lastEdge = targetPath.getLastEdge();
            
            switch (lastEdge.getType()) {
            case RECURSIVE_ROOT:
            case RECURSIVE_SUB:
                final Prerequisites fromPrerequisites = lastEdge.getPrerequisites();
    
                if (fromPrerequisites == null) {
                    throw new IllegalStateException("## no prerequisites for target " + target.getTargetObject());
                }
                
                if (!fromPrerequisites.isRecursiveBuild()) {
                    throw new IllegalStateException();
                }
                
                addRecursiveBuildTargets(targetState, context, fromPrerequisites, target, logger);
                anyAdded = true;
                break;
                
            default:
                break;
            }
        }

        return anyAdded;
    }

    private static final Boolean DEBUG = false;
    
    private
    void addRecursiveBuildTargets(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            Prerequisites fromPrerequisites,
            TargetDefinition<?> target,
            TargetExecutorLogger logger) {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final RecursiveBuildInfo<TaskContext, Object, Object>
                recursiveBuildInfo = (RecursiveBuildInfo)fromPrerequisites.getRecursiveBuildInfo();
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final BiFunction<Object, Object, Collection<Object>> getSubPrerequisites
            = (BiFunction)recursiveBuildInfo.getSubPrerequisitesFunction();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Function<Object, Object> getTargetFromPrerequisite = (Function)fromPrerequisites.getRecursiveBuildInfo().getTargetFromPrerequisiteFunction();
        
        final Object targetObject = getTargetFromPrerequisite.apply(target.getTargetObject());
        
        final LogContext logContext = target.getLogContext();
        
        if (DEBUG) {
            System.out.println("## got target object " + targetObject + " from " + target.getTargetObject() + " of " + target.getTargetObject().getClass());
        }

        final Collection<Object> targetPrerequisites = getSubPrerequisites.apply(context, targetObject);

        /*
        if (targetPrerequisites.size() != target.getPrerequisites().size()) {
            throw new IllegalStateException("prerequisites mismatch for "  + target + " " + targetPrerequisites + "/" + target.getPrerequisites());
        }
        */
        
        final TargetPaths prevPaths = executorState.getPaths(target.getTargetKey());
        
        final List<Prerequisite<?>> targetPrerequisitesList = new ArrayList<>(targetPrerequisites.size());
        
        for (Object subPrerequisiteObject : targetPrerequisites) {

            if (DEBUG) {
                System.out.println("## process sub prerequisite object " + subPrerequisiteObject);
            }
            
            final Collection<Object> subPrerequisitesList = getSubPrerequisites.apply(context, subPrerequisiteObject);

            final List<Prerequisite<?>> list = subPrerequisitesList.stream()
                    .map(sp -> new Prerequisite<>(logContext, sp, (File)null))
                    .collect(Collectors.toList());
            
            final Prerequisites subPrerequisites = new Prerequisites(
                    logContext,
                    list,
                    fromPrerequisites.getDescription(),
                    fromPrerequisites.getRecursiveBuildInfo(),
                    fromPrerequisites.getProducers());
            
            final TargetDefinition<Object> subTarget =
                    recursiveBuildInfo.createTarget(
                            logContext,
                            context,
                            subPrerequisiteObject,
                            Arrays.asList(subPrerequisites));

            final Prerequisite<?> subPrerequisite = new Prerequisite<>(logContext, subPrerequisiteObject, subTarget);
            
            targetPrerequisitesList.add(subPrerequisite);
            
            logger.onAddRecursiveTarget(target, subTarget);

            if (DEBUG) {
                System.out.println("## added subtarget " + subTarget + " from prerequisites " + targetPrerequisites + " from " + target.getTargetObject());
            }
            
            if (!executorState.hasTarget(subTarget.getTargetKey())) {
                if (DEBUG) {
                    System.out.println("## added target to execute " + subTarget);
                }

                executorState.addTargetToExecute(
                        subTarget,
                        prevPaths,
                        subPrerequisites,
                        subPrerequisite,
                        logger);
            }
            
            if (DEBUG) {
                System.out.println("## added subtarget done");
            }
        }
        
        // Trigger fromPrerequisite to be set in sub targets
        final Prerequisites updatedPrerequisites = new Prerequisites(
                logContext,
                targetPrerequisitesList,
                fromPrerequisites.getDescription(),
                fromPrerequisites.getRecursiveBuildInfo(),
                fromPrerequisites.getProducers());
        
        target.updatePrerequisites(Arrays.asList(updatedPrerequisites));
        
        /*
        final Target<?> replaceTarget = targetSpec.createTarget(
                context,
                target.getTargetObject(),
                Arrays.asList(new Prerequisites(targetPrerequisitesList, prerequisites.getSpec())));
        
        targetState.replaceTarget(target, replaceTarget);
        */
        
    }
}
