package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectSubProducts;
import com.neaterbits.util.concurrency.dependencyresolution.executor.CollectSubTargets;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Collectors;
import com.neaterbits.util.concurrency.dependencyresolution.executor.RecursiveBuildInfo;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class PrerequisiteSpec<CONTEXT extends TaskContext, TARGET, PREREQUISITE> {

	private final String named;
	
	private final String description;
	private final Class<?> productType;
	private final Class<?> itemType;
	private final Constraint constraint;
	private final BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites;
	private final RecursiveBuildInfo<CONTEXT, TARGET, PREREQUISITE> recursiveBuildInfo;
	private final Function<TARGET, PREREQUISITE> getSingleFrom;
	private final Function<PREREQUISITE, File> getSingleFile;
	private final BuildSpec<CONTEXT, PREREQUISITE> action;
	private final Collectors<TARGET> collectors;
	
	public PrerequisiteSpec(String named) {
		this(named, null, null, null, null, null, null, null, null, null, null);
	}

	public PrerequisiteSpec(
			String named,
			String description,
			Class<?> productType,
			Class<?> itemType,
			Constraint constraint,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			RecursiveBuildInfo<CONTEXT, TARGET, PREREQUISITE> recursiveBuildInfo,
			Function<TARGET, PREREQUISITE> getSingleFrom,
			Function<PREREQUISITE, File> getSingleFile,
			BuildSpec<CONTEXT, PREREQUISITE> action,
			Collectors<TARGET> collectors) {

		if (named == null) {
			if (getSingleFrom == null || getSingleFile == null) {
				Objects.requireNonNull(getPrerequisites);
			}
		}
		else {
			if (named.isEmpty()) {
				throw new IllegalArgumentException();
			}
			
			if (!named.trim().equals(named)) {
				throw new IllegalArgumentException();
			}
		}
		
		if (recursiveBuildInfo != null) {
			Objects.requireNonNull(action);
		}
		
		this.named = named;
		
		this.description = description;
		
		this.productType = productType;
		this.itemType = itemType;
		
		this.constraint = constraint;
		this.getPrerequisites = getPrerequisites;

		this.recursiveBuildInfo = recursiveBuildInfo;
		
		this.getSingleFrom = getSingleFrom;
		this.getSingleFile = getSingleFile;
		
		this.action = action;

		this.collectors = collectors;
	}
	
	String getNamed() {
		return named;
	}

	String getDescription() {
		return description;
	}

	Class<?> getProductType() {
		return productType;
	}

	Class<?> getItemType() {
		return itemType;
	}

	Constraint getConstraint() {
		return constraint;
	}

	Collection<PREREQUISITE> getPrerequisites(CONTEXT context, TARGET target) {
		
		Objects.requireNonNull(context);
		
		final Collection<PREREQUISITE> result;
		
		if (getPrerequisites != null) {
			result = getPrerequisites.apply(context, target);
		}
		else if (getSingleFrom != null && getSingleFile != null) {
			
			final PREREQUISITE prerequisite = getSingleFrom.apply(target);
			
			result = Arrays.asList(prerequisite);
		}
		else {
			throw new UnsupportedOperationException();
		}
		
		return result;
	}
	
	BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisitesFunction() {
		return getPrerequisites;
	}
	
	BiFunction<CONTEXT, ?, Collection<PREREQUISITE>> getSubPrerequisitesFunction() {
		return recursiveBuildInfo.getSubPrerequisitesFunction();
	}
	
	Function<PREREQUISITE, TARGET> getTargetFromPrerequisiteFunction() {
		
		return recursiveBuildInfo.getTargetFromPrerequisiteFunction();
	}

	boolean isRecursiveBuild() {
		return recursiveBuildInfo != null;
	}

	Function<PREREQUISITE, File> getSingleFileFunction() {
		return getSingleFile;
	}

	BuildSpec<CONTEXT, PREREQUISITE> getAction() {
		return action;
	}

	Collectors<TARGET> getCollectors() {
		return collectors;
	}
	
	RecursiveBuildInfo<CONTEXT, TARGET, PREREQUISITE> getRecursiveBuildInfo() {
		return recursiveBuildInfo;
	}

	CollectSubTargets<TARGET> getCollectSubTargets() {
		return collectors.getCollectSubTargets();
	}

	CollectSubProducts<TARGET> getCollectSubProducts() {
		return collectors.getCollectSubProducts();
	}
}
