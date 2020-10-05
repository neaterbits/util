package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;
import com.neaterbits.util.concurrency.dependencyresolution.executor.BuildEntity;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Collectors;
import com.neaterbits.util.concurrency.dependencyresolution.executor.RecursiveBuildInfo;

public final class Prerequisites extends BuildEntity implements Loggable {

	private static final String LOG_FIELD_PREREQUISITES = "prerequisitelist";
	
	private final int constructorLogSequenceNo; 
	private final Collection<Prerequisite<?>> prerequisites;
	private final String description;
	private final RecursiveBuildInfo<?, ?, ?> recursiveBuildInfo;
	private final Collectors<?> collectors;
	
	private TargetReference<?> fromTarget;

	private static String getLogIdentifierValue() {
		return null;
	}

	private static String getLogLocalIdentifierValue() {
		return null;
	}

	public Prerequisites(
			LogContext logContext,
			Collection<Prerequisite<?>> prerequisites,
			String description,
			RecursiveBuildInfo<?, ?, ?> recursiveBuildInfo,
			Collectors<?> collectors) {
		
		final String identifier = getLogIdentifierValue();
		
		this.constructorLogSequenceNo = logConstructor(
				logContext,
				this,
				Prerequisites.class,
				identifier,
				getLogLocalIdentifierValue(),
				description);
		
		Objects.requireNonNull(prerequisites);
	
		final Collection<Prerequisite<?>> logged = logConstructorCollectionField(logContext, LOG_FIELD_PREREQUISITES, prerequisites);
		
		this.prerequisites = logged != null ? Collections.unmodifiableCollection(logged) : null;
		this.description = description;
		this.recursiveBuildInfo = recursiveBuildInfo;
		this.collectors = collectors;

		prerequisites.forEach(prerequisite -> prerequisite.setFromPrerequisites(this));
	}
	
	@Override
	public int getConstructorLogSequenceNo() {
		return constructorLogSequenceNo;
	}

	@Override
	public String getLogIdentifier() {
		return getLogIdentifierValue();
	}

	@Override
	public String getLogLocalIdentifier() {
		return getLogLocalIdentifierValue();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDebugString() {
		return null;
	}

	@Override
	public BuildEntity getFromEntity() {
		return fromTarget.getTargetDefinitionIfAny();
	}

	public TargetReference<?> getFromTarget() {
		return fromTarget;
	}

	void setFromTarget(TargetReference<?> fromTarget) {
		
		Objects.requireNonNull(fromTarget);
		
		this.fromTarget = fromTarget;
	}

	public Collection<Prerequisite<?>> getPrerequisites() {
		return prerequisites;
	}

	public RecursiveBuildInfo<?, ?, ?> getRecursiveBuildInfo() {
		return recursiveBuildInfo;
	}

	public boolean isRecursiveBuild() {
		return recursiveBuildInfo != null;
	}

	public Collectors<?> getCollectors() {
		return collectors;
	}
	
	@Override
	public String toString() {
		return prerequisites.toString();
	}
}
