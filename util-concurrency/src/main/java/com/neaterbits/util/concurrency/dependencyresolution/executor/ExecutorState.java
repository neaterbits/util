package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogState;
import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetKey;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionParameters;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class ExecutorState<CONTEXT extends TaskContext> implements ActionParameters<Object>, TargetExecutorLogState {

	private static class TargetState<CTX extends TaskContext> {
	    
		private final TargetDefinition<?> definition;
		private final TargetStateMachine<CTX> stateMachine;
        private final TargetPaths paths;
		
		TargetState(
		        TargetDefinition<?> definition,
		        TargetStateMachine<CTX> stateMachine,
		        TargetPath path) {
		    
		    Objects.requireNonNull(definition);
		    Objects.requireNonNull(stateMachine);
		    Objects.requireNonNull(path);
		    
			this.definition = definition;
			this.stateMachine = stateMachine;

			this.paths = new TargetPaths(path);
		}

		TargetState(
                TargetDefinition<?> definition,
                TargetStateMachine<CTX> stateMachine,
                TargetPaths path) {
            
            Objects.requireNonNull(definition);
            Objects.requireNonNull(stateMachine);
            Objects.requireNonNull(path);
            
            this.definition = definition;
            this.stateMachine = stateMachine;

            this.paths = path;
        }
		
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((definition == null) ? 0 : definition.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final TargetState<?> other = (TargetState<?>) obj;
            if (definition == null) {
                if (other.definition != null)
                    return false;
            } else if (!definition.equals(other.definition))
                return false;
            return true;
        }
	}
	
	private final Map<TargetKey<?>, TargetState<CONTEXT>> targets;
	private final Map<Object, TargetKey<?>> targetsByTargetObject;

	private final List<TargetStateMachine<CONTEXT>> nonCompletedTargets;
	
	private final Map<TargetKey<?>, CollectedTargetObjects> recursiveTargetCollected;

	private final Map<TargetKey<?>, List<CollectedProduct>> collectedProductObjects;
	
	static <CTX extends TaskContext> ExecutorState<CTX> createFromTargetTree(
			TargetDefinition<?> rootTarget,
			TargetExecutor targetExecutor,
			TargetExecutorLogger logger) {

		final Map<TargetKey<?>, TargetState<CTX>> toExecuteTargets = new HashMap<>();

		Objects.requireNonNull(rootTarget);
		
		final TargetState<CTX> targetState = new TargetState<>(
		        rootTarget,
		        new TargetStateMachine<>(rootTarget, logger),
		        new TargetPath(rootTarget));
		
		toExecuteTargets.put(rootTarget.getTargetKey(), targetState);
		
		getSubTargets(
		        rootTarget,
		        toExecuteTargets,
		        targetState.paths,
		        logger);
		
		final ExecutorState<CTX> state = new ExecutorState<>(toExecuteTargets, logger);
		
		return state;
	}
	
	private ExecutorState(Map<TargetKey<?>, TargetState<CONTEXT>> toExecuteTargets, TargetExecutorLogger logger) {

		this.targets = new HashMap<>(toExecuteTargets);
		
		this.targetsByTargetObject = new HashMap<>(toExecuteTargets.size());
		
		this.nonCompletedTargets = new ArrayList<>(
						targets.values().stream()
							.map(target -> target.stateMachine)
							.collect(Collectors.toList()));
		
		for (TargetState<?> targetState : toExecuteTargets.values()) {
		    
		    final TargetDefinition<?> target = targetState.definition;
			
			final Object targetObject = target.getTargetObject();
			
			final boolean isRoot = targetState.paths.isEmpty();
			
			if (targetObject == null && !isRoot) {
				throw new IllegalArgumentException("No target object for " + target);
			}
			
			if (targetsByTargetObject.put(targetObject, target.getTargetKey()) != null) {
				throw new IllegalArgumentException();
			}
		}
		
		this.recursiveTargetCollected = new HashMap<>();
		
		// this.collectedTargetObjects = new HashMap<>();
		this.collectedProductObjects = new HashMap<>();
	}

	public boolean hasUnfinishedTargets() {
		return !nonCompletedTargets.isEmpty();
	}

	boolean hasTarget(TargetKey<?> target) {
		Objects.requireNonNull(target);
		
		return targets.containsKey(target);
	}
	
	TargetPaths getPaths(TargetKey<?> targetKey) {
	    
	    return targets.get(targetKey).paths;
	}
	
	void addTargetToExecute(
            TargetDefinition<?> target,
            TargetPaths prevPaths,
	        Prerequisites prevPrerequisites,
	        Prerequisite<?> prevPrerequisite,
	        TargetExecutorLogger logger) {

		Objects.requireNonNull(target);
		
		final Object targetObject = target.getTargetObject();
		
		if (targets.containsKey(target.getTargetKey())) {
			throw new IllegalArgumentException();
		}

		if (targetsByTargetObject.containsKey(targetObject)) {
			throw new IllegalStateException();
		}

		final TargetStateMachine<CONTEXT> targetStateMachine = new TargetStateMachine<>(target, logger);
		
		if (targets.containsKey(target.getTargetKey())) {
			throw new IllegalStateException();
		}
	
		final TargetPaths paths = prevPaths.add(prevPrerequisites, prevPrerequisite, target);
		
		targets.put(target.getTargetKey(), new TargetState<CONTEXT>(target, targetStateMachine, paths));
		targetsByTargetObject.put(targetObject, target.getTargetKey());
		
		nonCompletedTargets.add(targetStateMachine);
	}
	
	private Set<TargetDefinition<?>> targetsInState(Status status) {

		Objects.requireNonNull(status);
	
		return Collections.unmodifiableSet(targets.entrySet().stream()
				.filter(entry -> entry.getValue().stateMachine.getStatus() == status)
				.map(entry -> entry.getValue().definition)
				.collect(Collectors.toSet()));
	}
	
	@Override
	public Set<TargetDefinition<?>> getToExecuteTargets() {
		return targetsInState(Status.TO_EXECUTE);
	}

	public Collection<TargetStateMachine<CONTEXT>> getNonCompletedTargets() {
		return Collections.unmodifiableCollection(nonCompletedTargets);
	}

	@Override
	public Set<TargetDefinition<?>> getCompletedTargets() {
		return targetsInState(Status.SUCCESS);
	}

	@Override
	public Map<TargetDefinition<?>, Exception> getFailedTargets() {
		
		final Map<TargetDefinition<?>, Exception> map = new HashMap<>();
		
		for (Map.Entry<TargetKey<?>, TargetState<CONTEXT>> entry : targets.entrySet()) {
			if (entry.getValue().stateMachine.getStatus() == Status.FAILED) {
				map.put(entry.getValue().definition, entry.getValue().stateMachine.getException());
			}
		}
		
		return map;
	}

	@Override
	public Set<TargetDefinition<?>> getScheduledTargets() {
		return targetsInState(Status.SCHEDULED);
	}

	@Override
	public Set<TargetDefinition<?>> getActionPerformedCollectTargets() {
		return targetsInState(Status.ACTION_PERFORMED_COLLECTING_SUBTARGETS);
	}

	@Override
	public Status getTargetStatus(TargetDefinition<?> target) {

		final TargetStateMachine<?> targetState = targetState(target.getTargetKey());

		if (targetState == null) {

			printTargetKeys();
			
			throw new IllegalArgumentException("No target state for " + target.targetSimpleLogString() + " of type " + target.getTargetKey());
		}
		
		return targetState.getStatus();
	}
	
	PrerequisiteCompletion getTargetCompletion(TargetDefinition<?> target) {

		Objects.requireNonNull(target);

		final TargetState<?> targetState = targets.get(target.getTargetKey());

		if (targetState == null) {
			
			printTargetKeys();
			
			throw new IllegalStateException("No target state for " + target.targetSimpleLogString() + "/" + target.getTargetKey());
		}
		
		final TargetStateMachine<?> stateMachine = targetState.stateMachine;
		
		final Status status = stateMachine.getStatus();
		
		return status != Status.FAILED
				? new PrerequisiteCompletion(status)
				: new PrerequisiteCompletion(status, stateMachine.getException());
		
	}

	TargetDefinition<?> getTargetDefinition(TargetKey<?> targetKey) {
		
		Objects.requireNonNull(targetKey);
	
		final TargetState<CONTEXT> targetState = targets.get(targetKey);
		
		return targetState != null ? targetState.definition : null;
	}
	
	private TargetStateMachine<CONTEXT> targetState(TargetKey<?> target) {
		
		Objects.requireNonNull(target);
		
		final TargetState<CONTEXT> targetState = targets.get(target);
		
		return targetState != null ? targetState.stateMachine : null;
	}
	
	void addToRecursiveTargetCollected(TargetDefinition<?> target, CollectedTargetObjects collected) {
		
		Objects.requireNonNull(target);
		Objects.requireNonNull(collected);
		
		CollectedTargetObjects objects = recursiveTargetCollected.get(target.getTargetKey());
		
		// System.out.println("## merge collected " + collected + " with " + objects);
		
		if (objects != null) {
			objects = objects.mergeWith(collected);
		}
		else {
			objects = collected;
		}

		recursiveTargetCollected.put(target.getTargetKey(), objects);
	}
	
	CollectedTargetObjects getRecursiveTargetCollected(TargetDefinition<?> targetReference) {
		
		Objects.requireNonNull(targetReference);
		
		return recursiveTargetCollected.get(targetReference.getTargetKey());
	}

	void onCompletedTarget(TargetKey<?> target) {

		final TargetStateMachine<CONTEXT> targetState = targetState(target);
		
		if (targetState == null) {
			throw new IllegalStateException();
		}

		nonCompletedTargets.remove(targetState);
		
	}

	/*
	void addCollectedTargetObject(Target<?> target, CollectedTargetObject collected) {
		
		Objects.requireNonNull(target);
		Objects.requireNonNull(collected);
		
		if (collectedTargetObjects.containsKey(target)) {
			throw new IllegalStateException();
		}
		
		collectedTargetObjects.put(target, collected);
	}
	*/

	void addCollectedProduct(TargetKey<?> target, CollectedProduct collected) {
		
		Objects.requireNonNull(target);
		Objects.requireNonNull(collected);
		
		List<CollectedProduct> list = collectedProductObjects.get(target);
		
		if (list == null) {
			list = new ArrayList<>();
			collectedProductObjects.put(target, list);
		}
		
		list.add(collected);
	}

	/*
	CollectedTargetObject getCollectedTargetObject(Target<?> target) {
		
		Objects.requireNonNull(target);
		
		return collectedTargetObjects.get(target);
	}
	*/

	List<CollectedProduct> getCollectedProducts(TargetKey<?> target) {

		Objects.requireNonNull(target);
		
		final List<CollectedProduct> collectedProducts = collectedProductObjects.get(target);
		
		return collectedProducts;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCollectedProduct(Object targetObject, Class<T> type) {

		Objects.requireNonNull(targetObject);
		Objects.requireNonNull(type);
		
		final TargetKey<?> target = targetsByTargetObject.get(targetObject);

		// System.out.println("## collected products " + targetsByTargetObject + ", returns " + target + " for " + targetObject);
		
		if (target == null) {
			throw new IllegalStateException();
		}
		
		final List<CollectedProduct> collectedProducts = collectedProductObjects.get(target);
		
		return collectedProducts != null
					? (T)collectedProducts.stream()
							.filter(product -> product.getProductType().equals(type))
							.map(CollectedProduct::getProductObject)
							.findFirst()
							.orElse(null)
					: null;
	}

	private static <CTX extends TaskContext, TARGET>
	void getSubTargets(
	        TargetDefinition<TARGET> target,
	        Map<TargetKey<?>, TargetState<CTX>> toExecuteTargets,
	        TargetPaths paths,
	        TargetExecutorLogger logger) {
		
		for (Prerequisites prerequisites : target.getPrerequisites()) {
		    
			for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {
			    
				if (prerequisite.getSubTarget() != null) {

					final TargetDefinition<?> targetDefinition = prerequisite.getSubTarget();

					// System.out.println("## add subtarget " + prerequisite.getSubTarget() + "/" + targetDefinition);
					
					if (targetDefinition != null) {

					    final TargetPaths targetDefinitionPath = paths.add(prerequisites, prerequisite, targetDefinition);
					    
					    if (toExecuteTargets.containsKey(targetDefinition.getTargetKey())) {
							// eg external modules are prerequisites via multiple paths
						}
						else {
						    
						    final TargetStateMachine<CTX> stateMachine = new TargetStateMachine<>(targetDefinition, logger);
						    final TargetState<CTX> targetState = new TargetState<>(targetDefinition, stateMachine, targetDefinitionPath);

							toExecuteTargets.put(targetDefinition.getTargetKey(), targetState);
						}

						getSubTargets(targetDefinition, toExecuteTargets, targetDefinitionPath, logger);
					}
				}
				else {
					// Already existing file
				}
			}
		}
	}

    private void printTargetKeys() {
        
        System.out.println("targets " + targets.size());
        
        for (TargetKey<?> key : targets.keySet()) {
        
            System.out.println("target " + key);
            
        }
    }
}
