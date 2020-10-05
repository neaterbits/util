package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetReference;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionParameters;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class ExecutorState<CONTEXT extends TaskContext> implements ActionParameters<Object>, TargetExecutorLogState {

	
	private static class Target<CTX extends TaskContext> {
		private final TargetDefinition<?> definition;
		private final TargetStateMachine<CTX> stateMachine;
		
		public Target(TargetDefinition<?> definition, TargetStateMachine<CTX> stateMachine) {
			this.definition = definition;
			this.stateMachine = stateMachine;
		}
	}
	
	private final Map<TargetKey<?>, Target<CONTEXT>> targets;
	private final Map<Object, TargetKey<?>> targetsByTargetObject;

	private final List<TargetStateMachine<CONTEXT>> nonCompletedTargets;
	
	private final Map<TargetKey<?>, CollectedTargetObjects> recursiveTargetCollected;

	private final Map<TargetKey<?>, List<CollectedProduct>> collectedProductObjects;
	
	static <CTX extends TaskContext> ExecutorState<CTX> createFromTargetTree(
			TargetDefinition<?> rootTarget,
			TargetExecutor targetExecutor,
			TargetExecutorLogger logger) {

		final Set<TargetDefinition<?>> toExecuteTargets = new HashSet<>();

		Objects.requireNonNull(rootTarget);
		
		toExecuteTargets.add(rootTarget);
		
		getSubTargets(rootTarget, toExecuteTargets);
		
		final ExecutorState<CTX> state = new ExecutorState<>(toExecuteTargets, logger);
		
		return state;
	}
	
	private ExecutorState(Set<TargetDefinition<?>> toExecuteTargets, TargetExecutorLogger logger) {

		this.targets = toExecuteTargets.stream()
				.collect(Collectors.toMap(TargetDefinition::getTargetKey, target -> new Target<>(target, new TargetStateMachine<CONTEXT>(target, logger))));
		
		this.targetsByTargetObject = new HashMap<>(toExecuteTargets.size());
		
		this.nonCompletedTargets = new ArrayList<>(
						targets.values().stream()
							.map(target -> target.stateMachine)
							.collect(Collectors.toList()));
		
		for (TargetDefinition<?> target : toExecuteTargets) {
			
			final Object targetObject = target.getTargetObject();
			
			if (targetObject == null && !target.isRoot()) {
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
	
	void addTargetToExecute(TargetDefinition<?> target, TargetExecutorLogger logger) {

		Objects.requireNonNull(target);
		
		final Object targetObject = target.getTargetObject();
		
		if (targets.containsKey(target.getTargetKey())) {
			throw new IllegalArgumentException();
		}

		if (targetsByTargetObject.containsKey(targetObject)) {
			throw new IllegalStateException();
		}

		final TargetStateMachine<CONTEXT> targetState = new TargetStateMachine<>(target, logger);
		
		if (targets.containsKey(target.getTargetKey())) {
			throw new IllegalStateException();
		}
		
		targets.put(target.getTargetKey(), new Target<>(target, targetState));
		targetsByTargetObject.put(targetObject, target.getTargetKey());
		
		nonCompletedTargets.add(targetState);
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
		
		for (Map.Entry<TargetKey<?>, Target<CONTEXT>> entry : targets.entrySet()) {
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

	public final void printTargetKeys() {
		
		System.out.println("## targets " + targets.size());
		
		for (TargetKey<?> key : targets.keySet()) {
		
			System.out.println("## target " + key);
			
		}
	}
	
	PrerequisiteCompletion getTargetCompletion(TargetDefinition<?> target) {

		Objects.requireNonNull(target);

		final Target<?> targetState = targets.get(target.getTargetKey());

		if (targetState == null) {
			
			printTargetKeys();
			
			throw new IllegalStateException("No target state for " + target.targetSimpleLogString() + "/" + target.getTargetReference());
		}
		
		final TargetStateMachine<?> stateMachine = targetState.stateMachine;
		
		final Status status = stateMachine.getStatus();
		
		return status != Status.FAILED
				? new PrerequisiteCompletion(status)
				: new PrerequisiteCompletion(status, stateMachine.getException());
		
	}

	TargetDefinition<?> getTargetDefinition(TargetKey<?> targetKey) {
		
		Objects.requireNonNull(targetKey);
	
		final Target<CONTEXT> targetState = targets.get(targetKey);
		
		return targetState != null ? targetState.definition : null;
	}
	
	private TargetStateMachine<CONTEXT> targetState(TargetKey<?> target) {
		
		Objects.requireNonNull(target);
		
		final Target<CONTEXT> targetState = targets.get(target);
		
		return targetState != null ? targetState.stateMachine : null;
	}
	
	void addToRecursiveTargetCollected(TargetReference<?> target, CollectedTargetObjects collected) {
		
		Objects.requireNonNull(target);
		Objects.requireNonNull(collected);
		
		if (!target.isTopOfRecursion()) {
			throw new IllegalArgumentException();
		}
		
		CollectedTargetObjects objects = recursiveTargetCollected.get(target);
		
		// System.out.println("## merge collected " + collected + " with " + objects);
		
		if (objects != null) {
			objects = objects.mergeWith(collected);
		}
		else {
			objects = collected;
		}

		recursiveTargetCollected.put(target, objects);
	}
	
	CollectedTargetObjects getRecursiveTargetCollected(TargetReference<?> targetReference) {
		
		Objects.requireNonNull(targetReference);
		
		if (!targetReference.isTopOfRecursion()) {
			throw new IllegalArgumentException();
		}
		
		return recursiveTargetCollected.get(targetReference);
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

	private static <TARGET> void getSubTargets(TargetDefinition<TARGET> target, Set<TargetDefinition<?>> toExecuteTargets) {
		
		for (Prerequisites prerequisites : target.getPrerequisites()) {
			
			for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {

				if (prerequisite.getSubTarget() != null) {

					final TargetDefinition<?> targetDefinition = prerequisite.getSubTarget().getTargetDefinitionIfAny();

					// System.out.println("## add subtarget " + prerequisite.getSubTarget() + "/" + targetDefinition);
					
					if (targetDefinition != null) {
						
						if (toExecuteTargets.contains(targetDefinition)) {
							// eg external modules are prerequisites via multiple paths
						}
						else {
							toExecuteTargets.add(targetDefinition);
						}

						getSubTargets(targetDefinition, toExecuteTargets);
					}
				}
				else {
					// Already existing file
				}
			}
		}
	}
}
