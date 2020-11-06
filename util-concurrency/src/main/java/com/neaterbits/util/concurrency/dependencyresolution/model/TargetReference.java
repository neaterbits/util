package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;

public class TargetReference<TARGET> extends TargetKey<TARGET> implements Loggable {

	private final int constructorSequenceNo;
	
	private final Function<TARGET, String> description;
	
	private TargetDefinition<TARGET> targetDefinition;
	
	private Prerequisite<?> fromPrerequisite;
	
	public TargetReference(
			LogContext logContext,
			Class<TARGET> type,
			TARGET targetObject,
			Function<TARGET, String> description,
			boolean hasTargetDefinition) {
		
		super(type, targetObject);
	
		// Don't write this to log if has target definition, write that instead
		this.constructorSequenceNo = hasTargetDefinition
				? -1
				: logConstructor(logContext, this, TargetReference.class, null, null, description != null ? description.apply(targetObject) : null);
		
		this.description = description;
	}
	
	@Override
	public int getConstructorLogSequenceNo() {
		return constructorSequenceNo;
	}
	
	@Override
	public String getLogIdentifier() {
		return null;
	}

	@Override
	public String getLogLocalIdentifier() {
		return null;
	}

	public final boolean isRoot() {
		return fromPrerequisite == null;
	}
	
	public final Prerequisite<?> getFromPrerequisite() {
		return fromPrerequisite;
	}

	public final TargetDefinition<TARGET> getTargetDefinitionIfAny() {
		return targetDefinition;
	}
	
	final void setTargetDefinition(TargetDefinition<TARGET> targetDefinition) {
		
		Objects.requireNonNull(targetDefinition);
		
		this.targetDefinition = targetDefinition;
	}

	void setFromPrerequisite(Prerequisite<?> fromPrerequisite) {
		
		if (this.fromPrerequisite != null) {
			throw new IllegalStateException();
		}
		
		this.fromPrerequisite = fromPrerequisite;
	}

	public final String getDescription() {
		return description != null ? description.apply(getTargetObject()) : null;
	}

	final Function<TARGET, String> getDescriptionFunction() {
		return description;
	}
	
	@Override
	public String toString() {
		return "TargetReference [description=" + getDescription() + ", toString()=" + super.toString() + "]";
	}
}
