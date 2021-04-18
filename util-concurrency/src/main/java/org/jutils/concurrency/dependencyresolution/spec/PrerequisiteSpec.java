package org.jutils.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.coll.Coll;
import org.jutils.concurrency.dependencyresolution.executor.ProduceFromSubProducts;
import org.jutils.concurrency.dependencyresolution.executor.ProduceFromSubTargets;
import org.jutils.concurrency.dependencyresolution.executor.Producers;
import org.jutils.concurrency.dependencyresolution.executor.RecursiveBuildInfo;
import org.jutils.concurrency.dependencyresolution.executor.RecursiveBuildSpec;
import org.jutils.concurrency.dependencyresolution.executor.SubPrerequisites;
import org.jutils.concurrency.dependencyresolution.executor.RecursiveBuildInfo.CreateTargetDefinition;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

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
	private final BuildSpec<CONTEXT, PREREQUISITE> buildSpec;
	private final Producers<TARGET> collectors;
	
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
			RecursiveBuildSpec<CONTEXT, TARGET, PREREQUISITE> recursiveBuildSpec,
			Function<TARGET, PREREQUISITE> getSingleFrom,
			Function<PREREQUISITE, File> getSingleFile,
			BuildSpec<CONTEXT, PREREQUISITE> action,
			Producers<TARGET> collectors) {

		if (named == null) {
			if (getSingleFrom == null && getPrerequisites == null) {
			    throw new IllegalStateException("No target for '" + description + "'");
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
		
		if (recursiveBuildSpec != null) {
			Objects.requireNonNull(action);
		}
		
		this.named = named;
		
		this.description = description;
		
		this.productType = productType;
		this.itemType = itemType;
		
		this.constraint = constraint;
		this.getPrerequisites = getPrerequisites;

        final CreateTargetDefinition<CONTEXT, PREREQUISITE> createTargetDefinition
            = (LogContext logContext,
                CONTEXT context,
                PREREQUISITE target,
                List<Prerequisites> prerequisitesList) -> {
    
            return action.getSubTarget().createTargetDefinition(logContext, context, target, prerequisitesList);
        };

        this.recursiveBuildInfo = recursiveBuildSpec != null
                ? new RecursiveBuildInfo<>(recursiveBuildSpec, createTargetDefinition)
                : null;
        
		this.getSingleFrom = getSingleFrom;
		this.getSingleFile = getSingleFile;
		
		this.buildSpec = action;

		this.collectors = collectors;
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
			
			if (Coll.containsNull(result)) {
			    throw new IllegalStateException();
			}
		}
		else if (getSingleFrom != null) {
			
			final PREREQUISITE prerequisite = getSingleFrom.apply(target);
			
			if (prerequisite == null) {
			    throw new IllegalStateException();
			}
			
			result = Arrays.asList(prerequisite);
		}
		else if (named != null) {
		    @SuppressWarnings("unchecked")
            final PREREQUISITE prerequisite = (PREREQUISITE)named;
		    
		    result = Arrays.asList(prerequisite);
		}
		else {
			throw new UnsupportedOperationException("Cannot get prerequisites for " + this);
		}
		
		return result;
	}
	
	BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisitesFunction() {
		return getPrerequisites;
	}
	
	BiFunction<CONTEXT, ?, SubPrerequisites<PREREQUISITE>> getSubPrerequisitesFunction() {
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

	BuildSpec<CONTEXT, PREREQUISITE> getBuildSpec() {
		return buildSpec;
	}

	Producers<TARGET> getCollectors() {
		return collectors;
	}
	
	RecursiveBuildInfo<CONTEXT, TARGET, PREREQUISITE> getRecursiveBuildInfo() {

	    return recursiveBuildInfo;
	}

	ProduceFromSubTargets<TARGET> getCollectSubTargets() {
		return collectors.getProduceFromSubTargets();
	}

	ProduceFromSubProducts<TARGET> getCollectSubProducts() {
		return collectors.getProduceFromSubProducts();
	}

    @Override
    public String toString() {
        return "PrerequisiteSpec [named=" + named + ", description=" + description + ", productType=" + productType
                + ", itemType=" + itemType + ", constraint=" + constraint + "]";
    }
}
