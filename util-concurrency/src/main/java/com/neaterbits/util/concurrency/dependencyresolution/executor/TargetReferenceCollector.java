package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.HashSet;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetReference;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetReferenceCollector extends BaseCollector {
	
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
    
    @Override
    ProductAndTargets productFromCollectedSubTargets(
            TargetExecutionContext<?> context,
            
            TargetDefinition<?> target,
            Prerequisites withProduce) {
    
        
        final CollectedTargetObjects subTargetObjects = getCollectedTargetsFromSub(withProduce, context.state);
        
        if (DEBUG) {
            System.out.println("-- collected targets "  + subTargetObjects);
        }

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

        return collectedProduct != null
                ? new ProductAndTargets(subTargetObjects, collectedProduct)
                : null;
    }
}
