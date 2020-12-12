package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.coll.MapOfList;
import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetKey;
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

    private RecursiveBuildInfo<TaskContext, Object, Object>
    getRecursiveBuildInfo(Prerequisites fromPrerequisites) {
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final RecursiveBuildInfo<TaskContext, Object, Object>
                recursiveBuildInfo = (RecursiveBuildInfo)fromPrerequisites.getRecursiveBuildInfo();

        return recursiveBuildInfo;
    }
    
    private SubPrerequisites<Object> getSubPrerequisites(
            TaskContext context,
            RecursiveBuildInfo<?, ?, ?> recursiveBuildInfo,
            Object targetObject) {
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final BiFunction<Object, Object, SubPrerequisites<Object>> getSubPrerequisites
            = (BiFunction)recursiveBuildInfo.getSubPrerequisitesFunction();

        return getSubPrerequisites.apply(context, targetObject);
    }

    private
    void addRecursiveBuildTargets(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            Prerequisites fromPrerequisites,
            TargetDefinition<?> target,
            TargetExecutorLogger logger) {
        
        final RecursiveBuildInfo<TaskContext, Object, Object> recursiveBuildInfo
            = getRecursiveBuildInfo(fromPrerequisites);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Function<Object, Object> getTargetFromPrerequisite = (Function)fromPrerequisites.getRecursiveBuildInfo().getTargetFromPrerequisiteFunction();
        
        final Object targetObject = getTargetFromPrerequisite.apply(target.getTargetObject());
        
        if (DEBUG) {
            System.out.println("## got target object " + targetObject + " from " + target.getTargetObject() + " of " + target.getTargetObject().getClass());
        }

        final SubPrerequisites<Object> targetPrerequisites = getSubPrerequisites(context, recursiveBuildInfo, targetObject);

        /*
        if (targetPrerequisites.size() != target.getPrerequisites().size()) {
            throw new IllegalStateException("prerequisites mismatch for "  + target + " " + targetPrerequisites + "/" + target.getPrerequisites());
        }
        */
        
        processSubPrerequisites(executorState, context, target, fromPrerequisites, targetPrerequisites);
    }
    
    private void processSubPrerequisites(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            TargetDefinition<?> target,
            Prerequisites fromPrerequisites,
            SubPrerequisites<Object> targetPrerequisites) {
        
        if (targetPrerequisites.getList() != null) {
            
            processSubPrerequisitesList(
                    executorState,
                    context,
                    target,
                    fromPrerequisites,
                    targetPrerequisites.getList());
        }
        else if (targetPrerequisites.getMapOfList() != null) {
            
            processSubPrerequisitesMapOfList(
                    executorState,
                    context,
                    target,
                    fromPrerequisites,
                    targetPrerequisites.getPrerequisiteType(),
                    targetPrerequisites.getMapOfList());
        }
        else {
            throw new IllegalStateException();
        }
    }
    
    private void processSubPrerequisitesMapOfList(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            TargetDefinition<?> target,
            Prerequisites fromPrerequisites,
            Class<?> targetType,
            MapOfList<Object, Object> targetPrerequisites) {
        
        for (Map.Entry<Object, List<Object>> entry : targetPrerequisites.entrySet()) {

            @SuppressWarnings("unchecked")
            final TargetKey<Object> targetKey = new TargetKey<>((Class<Object>)targetType, entry.getKey());
            
            final TargetDefinition<?> entryTarget = executorState.getTargetDefinition(targetKey);
            
            if (entryTarget == null) {
                throw new IllegalStateException();
            }
            
            processSubPrerequisitesList(
                    executorState,
                    context,
                    entryTarget,
                    fromPrerequisites,
                    entry.getValue());
        }
    }

    private void processSubPrerequisitesList(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            TargetDefinition<?> target,
            Prerequisites fromPrerequisites,
            Collection<Object> targetPrerequisites) {
        
        final List<Prerequisite<?>> targetPrerequisitesList = new ArrayList<>(targetPrerequisites.size());

        for (Object prerequisite : targetPrerequisites) {

            processSubPrerequisite(
                    executorState,
                    context,
                    target,
                    fromPrerequisites,
                    prerequisite,
                    targetPrerequisites,
                    targetPrerequisitesList);
        }
        
        updateTargetPrerequisites(
                target.getLogContext(),
                target,
                fromPrerequisites,
                targetPrerequisitesList);
    }

    private void processSubPrerequisite(
            ExecutorState<CONTEXT> executorState,
            TaskContext context,
            TargetDefinition<?> target,
            Prerequisites fromPrerequisites,
            Object subPrerequisiteObject,
            Collection<Object> targetPrerequisites,
            List<Prerequisite<?>> targetPrerequisitesList) {
        
        final RecursiveBuildInfo<TaskContext, Object, Object> recursiveBuildInfo
            = getRecursiveBuildInfo(fromPrerequisites);
        
        final LogContext logContext = target.getLogContext();

        final TargetPaths prevPaths = executorState.getPaths(target.getTargetKey());
        
        if (subPrerequisiteObject == null) {
            throw new IllegalStateException();
        }

        if (DEBUG) {
            System.out.println("## process sub prerequisite object " + subPrerequisiteObject);
        }

        /*
        final SubPrerequisites<Object> subPrerequisitesList = getSubPrerequisites(context, recursiveBuildInfo, subPrerequisiteObject);
        
        final Collection<Object> objList;
        
        if (subPrerequisitesList.isEmpty()) {
            objList = Collections.emptyList();
        }
        else if (subPrerequisitesList.getList() != null) {
            objList = subPrerequisitesList.getList();
        }
        else if (subPrerequisitesList.getMapOfList() != null) {
            if (subPrerequisitesList.getMapOfList().keyCount() != 1) {
                throw new IllegalStateException("More than one sub " + subPrerequisitesList.getMapOfList());
            }
            
            objList = subPrerequisitesList.getMapOfList().entrySet().iterator().next().getValue();
        }
        else {
            throw new IllegalStateException();
        }

        if (objList.contains(null)) {
            throw new IllegalStateException("null returned for " + subPrerequisiteObject);
        }

        final List<Prerequisite<?>> list = objList.stream()
                .map(sp -> new Prerequisite<>(logContext, sp))
                .collect(Collectors.toList());
        */
        
        final List<Prerequisite<?>> list = Collections.emptyList();
        
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
            System.out.println(
                      "## added subtarget " + subTarget
                    + " from prerequisites " + targetPrerequisites
                    + " from " + target.getTargetObject());
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

    private void updateTargetPrerequisites(
            LogContext logContext,
            TargetDefinition<?> target,
            Prerequisites fromPrerequisites,
            List<Prerequisite<?>> targetPrerequisitesList) {
        
        final Prerequisites updatedPrerequisites = new Prerequisites(
                logContext,
                targetPrerequisitesList,
                fromPrerequisites.getDescription(),
                fromPrerequisites.getRecursiveBuildInfo(),
                fromPrerequisites.getProducers());
        
        target.updatePrerequisites(Arrays.asList(updatedPrerequisites));
    }
}
