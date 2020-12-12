package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.executor.ProduceFromSubProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ProduceFromSubTargets;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Producers;
import com.neaterbits.util.concurrency.dependencyresolution.executor.RecursiveBuildSpec;
import com.neaterbits.util.concurrency.dependencyresolution.executor.SubPrerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.spec.BuildSpec;
import com.neaterbits.util.concurrency.dependencyresolution.spec.PrerequisiteSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class PrerequisiteBuilderState<CONTEXT extends TaskContext, TARGET, PRODUCT, ITEM> {

	private final String description;
	private final Class<PRODUCT> productType;
	private final Class<ITEM> itemType;

	private Constraint constraint;
	private BiFunction<CONTEXT, TARGET, Collection<?>> getPrerequisites;
	private BiFunction<CONTEXT, ?, Collection<?>> getSubPrerequisites;
	private Function<?, ?> getDependencyFromPrerequisite;
	
	private Function<TARGET, ?> getSingleFrom;
	private Function<?, File> getSingleFile;
	
	private boolean recursiveBuild;
	
	private BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubTargets;
	private BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubProducts;

	private BuildSpec<CONTEXT, ?> build;
	
	PrerequisiteBuilderState(String description, Class<PRODUCT> productType, Class<ITEM> itemType) {
		
		Objects.requireNonNull(description);
		
		this.description = description;
		this.productType = productType;
		this.itemType = itemType;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final <PREREQUISITE> void setIterating(
			Constraint constraint,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites) {
		
		Objects.requireNonNull(getPrerequisites);

		if (this.getPrerequisites != null) {
			throw new IllegalStateException();
		}
		
		this.constraint = constraint;
		this.getPrerequisites = (BiFunction)getPrerequisites;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final <PREREQUISITE, SUB_TARGET> void setIteratingAndBuildingRecursively(
			Constraint constraint,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			BiFunction<CONTEXT, SUB_TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
			Function<PREREQUISITE, SUB_TARGET> getDependencyFromPrerequisite) {
		
		Objects.requireNonNull(getPrerequisites);
		Objects.requireNonNull(getDependencyFromPrerequisite);

		if (this.getPrerequisites != null) {
			throw new IllegalStateException();
		}

		this.constraint = constraint;
		this.getPrerequisites = (BiFunction)getPrerequisites;
		this.getSubPrerequisites = (BiFunction)getSubPrerequisites;
		this.getDependencyFromPrerequisite = getDependencyFromPrerequisite;
		this.recursiveBuild = true;
	}

    final void setSingleFrom(Function<TARGET, ?> getSingleFrom) {
        
        Objects.requireNonNull(getSingleFrom);
        
        this.getSingleFrom = getSingleFrom;
    }
	
	final void setSingleFile(Function<?, File> getSingleFile) {
		
		Objects.requireNonNull(getSingleFile);
		
		this.getSingleFile = getSingleFile;
	}
	
	final void setProduceFromSubTargets(BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubTargets) {

		Objects.requireNonNull(produceFromSubTargets);
		
		if (this.produceFromSubTargets != null) {
			throw new IllegalStateException();
		}
		
		this.produceFromSubTargets = produceFromSubTargets;
	}

	final void setProduceFromSubProducts(BiFunction<TARGET, List<ITEM>, PRODUCT> produceFromSubProducts) {

		Objects.requireNonNull(produceFromSubProducts);
		
		if (this.produceFromSubProducts != null) {
			throw new IllegalStateException();
		}
		
		this.produceFromSubProducts = produceFromSubProducts;
	}

	final void setBuild(BuildSpec<CONTEXT, ?> build) {
		
		Objects.requireNonNull(build);
		
		this.build = build;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final PrerequisiteSpec<CONTEXT, TARGET, ?> build() {
	    
		return new PrerequisiteSpec<>(
				null,
				description,
				productType,
				itemType,
				constraint,
				(BiFunction)getPrerequisites,
				recursiveBuild
					? new RecursiveBuildSpec<>(
							(BiFunction)getSubPrerequisites,
							(Function)getDependencyFromPrerequisite)
					: null,
					
				(Function)getSingleFrom,
				(Function)getSingleFile,
				build,
				new Producers<>(
					produceFromSubTargets != null ? new ProduceFromSubTargets<>(productType, (BiFunction)produceFromSubTargets) : null,
					produceFromSubProducts != null ? new ProduceFromSubProducts<>(productType, (BiFunction)produceFromSubProducts) : null));
	}

	@Override
	public String toString() {
		return "PrerequisiteBuilderState [description=" + description + ", productType=" + productType + ", itemType="
				+ itemType + ", constraint=" + constraint + ", recursiveBuild=" + recursiveBuild + "]";
	}
}
