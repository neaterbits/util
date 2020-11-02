package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.HashSet;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

final class TargetPathCollector extends BaseCollector {

    @Override
    ProductAndTargets productFromCollectedSubTargets(
            TargetExecutionContext<?> context,
            TargetDefinition<?> target,
            Prerequisites withProduce) {

        CollectedProduct collectedProduct = null;

        if (!target.getPrerequisites().contains(withProduce)) {
            throw new IllegalArgumentException();
        }

        final ProduceFromSubTargets<?> produceFromSubTargets = withProduce.getProducers().getProduceFromSubTargets();

        CollectedTargetObjects productCollectedTargetObjects = null;

        for (Prerequisite<?> prerequisite : withProduce.getPrerequisites()) {

            final TargetDefinition<?> subTarget = prerequisite.getSubTarget().getTargetDefinitionIfAny();
            
            if (subTarget != null) {

                final TargetPaths subTargetPaths = context.state.getPaths(subTarget.getTargetKey());

                for (TargetPath subTargetPath : subTargetPaths.getPaths()) {
                    
                    final TargetEdge lastEdge = subTargetPath.getLastEdge();
                    final TargetEdge prevEdge = subTargetPath.getLastEdge(1);
                    
                    if (!lastEdge.getTarget().equals(subTarget)) {
                        throw new IllegalStateException();
                    }
                    
                    // The path matches this target that we are collecting for,
                    // a sub target may be a prerequisite of other targets too
                    // e.g. apache commons is included both directly from project
                    // and from other external dependency
                    if (prevEdge.getTarget().equals(target)) {
                        
                        // Collected objects based on current subtarget object
                        final Set<CollectedTargetObject> collectedSet = new HashSet<>();
                        collectedSet.add(new CollectedTargetObject(subTarget.getTargetObject()));
                        
                        CollectedTargetObjects collectedObjects = new CollectedTargetObjects(collectedSet);

                        switch (lastEdge.getType()) {

                        case RECURSIVE_ROOT: {

                            // Anything gathered from subtargets of this one ?
                            final CollectedTargetObjects subCollectedObjects
                                = context.state.getRecursiveTargetCollected(subTarget.getTargetReference());
                
                            if (subCollectedObjects != null) {
                                collectedObjects = subCollectedObjects.mergeWith(collectedObjects);
                            }

                            // Add to product since recursion root
                            productCollectedTargetObjects = productCollectedTargetObjects != null
                                    ? productCollectedTargetObjects.mergeWith(collectedObjects)
                                    : collectedObjects;
                            break;
                        }
                            
                        case RECURSIVE_SUB: {

                            // Anything gathered from subtargets of this one ?
                            final CollectedTargetObjects subCollectedObjects
                                = context.state.getRecursiveTargetCollected(subTarget.getTargetReference());
                    
                            if (subCollectedObjects != null) {
                                collectedObjects = subCollectedObjects.mergeWith(collectedObjects);
                            }

                            // Add to target above since not recursion root
                            context.state.addToRecursiveTargetCollected(target.getTargetReference(), collectedObjects);
                            break;
                        }
                        
                        case REGULAR: {
                         
                            // Merge up to previous level
                            
                            // Collect regular target objects
                            productCollectedTargetObjects = productCollectedTargetObjects != null
                                ? productCollectedTargetObjects.mergeWith(collectedObjects)
                                : collectedObjects;
                            break;
                        }
                            
                        case ROOT:
                            // Cannot be root as is a sub target
                        default:
                            throw new IllegalStateException();
                        
                        }
                    }
                }
            }
        }
        
        final ProductAndTargets productAndTargets;
        
        if (productCollectedTargetObjects != null) {

            context.logger.onAddTopRecursionCollected(target, withProduce, productCollectedTargetObjects);
            
            final Object collectTargetObject = target.getTargetObject();
            
            collectedProduct = produceFromSubTargets.collect(collectTargetObject, productCollectedTargetObjects);
            
            productAndTargets = new ProductAndTargets(productCollectedTargetObjects, collectedProduct);
        }
        else {
            productAndTargets = null;
        }

        return productAndTargets;
    }
}
