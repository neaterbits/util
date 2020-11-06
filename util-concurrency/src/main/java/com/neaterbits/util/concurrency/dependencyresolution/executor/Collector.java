package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetReference;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

class Collector {
	
	private static final Boolean DEBUG = false;
	
	private static <CONTEXT extends TaskContext>
	CollectedTargetObjects getCollectedTargetsFromSub(Prerequisites withProduce, ExecutorState<CONTEXT> targetState) {

		final Set<CollectedTargetObject> targetObjects = new HashSet<>(withProduce.getPrerequisites().size());
		
		for (Prerequisite<?> prerequisite : withProduce.getPrerequisites()) {

			final TargetReference<?> subTarget = prerequisite.getSubTarget();

			if (subTarget != null) {
				if (subTarget.getTargetObject() != null) {
					targetObjects.add(new CollectedTargetObject(subTarget.getTargetObject()));
				}
			}
		}
		
		return new CollectedTargetObjects(targetObjects);
	}
	
	private static CollectedProduct productFromCollectedSubTargets(
			TargetExecutionContext<?> context,
			
			TargetDefinition<?> target,
			Prerequisites withProduce,
			CollectedTargetObjects subTargetObjects) {
	
		if (!target.getPrerequisites().contains(withProduce)) {
			throw new IllegalArgumentException();
		}
		
		final ProduceFromSubTargets<?> produceFromSubTargets = withProduce.getProducers().getProduceFromSubTargets();
		final Object collectTargetObject;
		final CollectedProduct collectedProduct;
		
		if (target.isRecursionSubTarget()) {
			
			final TargetReference<?> topOfRecursionTargetReference = target.getTopOfRecursion();
			final TargetDefinition<?> topOfRecursionTarget = topOfRecursionTargetReference.getTargetDefinitionIfAny();

			if (DEBUG) {
				System.out.println("## add " + subTargetObjects + " for sub of " + topOfRecursionTarget);
			}
			
			context.logger.onAddSubRecursionCollected(topOfRecursionTarget, target, subTargetObjects);
			
			context.state.addToRecursiveTargetCollected(topOfRecursionTargetReference, subTargetObjects);

			if (target.isTopOfRecursion()) {
				// This is top of recursion so must add this too
				
				final Set<CollectedTargetObject> thisCollected = new HashSet<>();
				thisCollected.add(new CollectedTargetObject(target.getTargetObject()));

				final CollectedTargetObjects thisObj = new CollectedTargetObjects(thisCollected);
				
				context.logger.onAddSubRecursionCollected(topOfRecursionTarget, target, thisObj);
				context.state.addToRecursiveTargetCollected(topOfRecursionTargetReference, thisObj);
			}
			
			collectedProduct = null;
		}
		else {

			if (withProduce.isRecursiveBuild()) {
				// Target directly above recursion targets
				
				CollectedTargetObjects allCollectedTargetObjects = null;
				
				for (Prerequisite<?> prerequisite : withProduce.getPrerequisites()) {
					
					if (!prerequisite.getSubTarget().isTopOfRecursion()) {
						throw new IllegalStateException();
					}
					
					final CollectedTargetObjects collectedObjects
							= context.state.getRecursiveTargetCollected(prerequisite.getSubTarget());
					
					if (collectedObjects != null) {
						allCollectedTargetObjects = allCollectedTargetObjects != null
								? allCollectedTargetObjects.mergeWith(collectedObjects)
								: collectedObjects;
					}
				}
				
				context.logger.onAddTopRecursionCollected(target, withProduce, allCollectedTargetObjects);
				
				if (allCollectedTargetObjects != null) {
					collectTargetObject = target.getTargetObject();
					collectedProduct = produceFromSubTargets.collect(collectTargetObject, allCollectedTargetObjects);
				}
				else {
					collectedProduct = null;
				}
			}
			else {
				if (DEBUG) {
					System.out.println("## collect sub target objects " + subTargetObjects);
				}
	
				collectTargetObject = withProduce.getFromTarget().getTargetObject();
				collectedProduct = produceFromSubTargets.collect(collectTargetObject, subTargetObjects);
			}
		}
		
		return collectedProduct;
	}
	
	private static <CONTEXT extends TaskContext> void collectProductsFromSubTargetsOf(TargetExecutionContext<CONTEXT> context, TargetDefinition<?> target) {

		for (Prerequisites prerequisites : target.getPrerequisites()) {

			if (prerequisites.getProducers().getProduceFromSubTargets() != null) {

				if (DEBUG) {
					System.out.println("-- collect computed targets for " + target.getTargetObject());
				}

				final CollectedTargetObjects subTargetObjects = getCollectedTargetsFromSub(prerequisites, context.state);
				
				if (DEBUG) {
					System.out.println("-- collected targets "  + subTargetObjects);
				}
				
				final CollectedProduct collected = productFromCollectedSubTargets(context, target, prerequisites, subTargetObjects);
				
				if (collected != null) {
					
					if (context.logger != null) {
						context.logger.onCollectTargetObjects(target, subTargetObjects, collected, context.state);
					}
					
					if (DEBUG) {
						System.out.println("## add collected target product for " + target + " " + collected);
					}
					
					context.state.addCollectedProduct(target.getTargetKey(), collected);
				}
			}
		}
	}

	private static <CONTEXT extends TaskContext>
	CollectedProducts getCollectedProductsFromSub(TargetDefinition<?> target, Prerequisites withProduce, ExecutorState<CONTEXT> targetState) {

		final Set<CollectedProduct> subProducts = new HashSet<>(withProduce.getPrerequisites().size());
		
		for (Prerequisite<?> prerequisite : withProduce.getPrerequisites()) {

			final TargetReference<?> subTargetReference = prerequisite.getSubTarget();

			final List<CollectedProduct> targetSubProducts = targetState.getCollectedProducts(subTargetReference);
			
			if (DEBUG) {
				System.out.println("## collected subproducts for " + target + " from " + subTargetReference + " " + targetSubProducts);
			}
			
			if (targetSubProducts != null) {
				subProducts.addAll(targetSubProducts);
			}
		}
		
		return new CollectedProducts(subProducts);
	}

	private static CollectedProduct productFromCollectedSubProducts(
			TargetExecutionContext<?> context,
			TargetDefinition<?> target,
			Prerequisites withProduce,
			CollectedProducts subProducts) {
	
		if (!target.getPrerequisites().contains(withProduce)) {
			throw new IllegalArgumentException();
		}
		
		final ProduceFromSubProducts<?> produceFromSubProducts = withProduce.getProducers().getProduceFromSubProducts();
		final CollectedProduct collectedProduct;
		
		if (produceFromSubProducts == null) {
			collectedProduct = null;
		}
		else {
			
			if (withProduce.isRecursiveBuild()) {
				throw new UnsupportedOperationException();
			}
			else {
				collectedProduct = produceFromSubProducts.collect(target.getTargetObject(), subProducts);
			}
		}
		
		return collectedProduct;
	}

	private static <CONTEXT extends TaskContext> void collectProductsFromSubProductsOf(TargetExecutionContext<CONTEXT> context, TargetDefinition<?> target) {

		for (Prerequisites prerequisites : target.getPrerequisites()) {

			if (prerequisites.getProducers().getProduceFromSubProducts() != null) {

				if (DEBUG) {
					System.out.println("-- collect computed products for " + " with prerequisites " + prerequisites);
				}

				final CollectedProducts subProducts = getCollectedProductsFromSub(target, prerequisites, context.state);
				
				if (DEBUG) {
					System.out.println("-- collected products for " + target + " " + subProducts);
				}
	
				final CollectedProduct collected = productFromCollectedSubProducts(context, target, prerequisites, subProducts);
				
				if (collected != null) {
					
					if (context.logger != null) {
						context.logger.onCollectProducts(target, subProducts, collected, context.state);
					}
					
					if (DEBUG) {
						System.out.println("## add collected product for subproducts of " + target + " " + collected);
					}
					
					context.state.addCollectedProduct(target.getTargetKey(), collected);
				}
			}
		}
	}

	static <CONTEXT extends TaskContext> void collectFromSubTargetsAndSubProducts(TargetExecutionContext<CONTEXT> context, TargetDefinition<?> target) {
		
		collectProductsFromSubTargetsOf(context, target);
		collectProductsFromSubProductsOf(context, target);
	}
}
