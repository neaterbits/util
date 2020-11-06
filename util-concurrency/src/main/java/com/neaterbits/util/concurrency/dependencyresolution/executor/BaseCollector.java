package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

abstract class BaseCollector {

    static class ProductAndTargets {
        
        final CollectedTargetObjects collectedTargetObjects;
        final CollectedProduct product;

        ProductAndTargets(CollectedTargetObjects collectedTargetObjects, CollectedProduct product) {
            
            Objects.requireNonNull(collectedTargetObjects);
            Objects.requireNonNull(product);
            
            this.collectedTargetObjects = collectedTargetObjects;
            this.product = product;
        }
    }

    abstract ProductAndTargets productFromCollectedSubTargets(
            TargetExecutionContext<?> context,
            TargetDefinition<?> target,
            Prerequisites withCollect);
    
    static final Boolean DEBUG = false;

    private static final BaseCollector COLLECTOR = new TargetPathCollector();
    
    static <CONTEXT extends TaskContext> void collectFromSubTargetsAndSubProducts(
            TargetExecutionContext<CONTEXT> context,
            TargetDefinition<?> target) {
        
        COLLECTOR.performCollectFromSubTargetsAndSubProducts(context, target);
    }
    
    private <CONTEXT extends TaskContext> void collectProductsFromSubTargetsOf(TargetExecutionContext<CONTEXT> context, TargetDefinition<?> target) {

        for (Prerequisites prerequisites : target.getPrerequisites()) {

            if (prerequisites.getProducers().getProduceFromSubTargets() != null) {

                if (DEBUG) {
                    System.out.println("-- collect computed targets for " + target.getTargetObject());
                }
                
                final ProductAndTargets collected = productFromCollectedSubTargets(context, target, prerequisites);
                
                if (collected != null) {
                    
                    if (context.logger != null) {
                        
                        context.logger.onCollectTargetObjects(
                                target,
                                collected.collectedTargetObjects,
                                collected.product,
                                context.state);
                    }
                    
                    if (DEBUG) {
                        System.out.println("## add collected target product for " + target + " " + collected);
                    }
                    
                    context.state.addCollectedProduct(target.getTargetKey(), collected.product);
                }
            }
        }
    }

    private static <CONTEXT extends TaskContext>
    CollectedProducts getCollectedProductsFromSub(TargetDefinition<?> target, Prerequisites withProduce, ExecutorState<CONTEXT> targetState) {

        final Set<CollectedProduct> subProducts = new HashSet<>(withProduce.getPrerequisites().size());
        
        for (Prerequisite<?> prerequisite : withProduce.getPrerequisites()) {

            final TargetDefinition<?> subTarget = prerequisite.getSubTarget();

            final List<CollectedProduct> targetSubProducts = targetState.getCollectedProducts(subTarget.getTargetKey());
            
            if (DEBUG) {
                System.out.println("## collected subproducts for " + target + " from " + subTarget + " " + targetSubProducts);
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

    private <CONTEXT extends TaskContext>
    void performCollectFromSubTargetsAndSubProducts(
            TargetExecutionContext<CONTEXT> context,
            TargetDefinition<?> target) {

        collectProductsFromSubTargetsOf(context, target);
        collectProductsFromSubProductsOf(context, target);
    }
}
