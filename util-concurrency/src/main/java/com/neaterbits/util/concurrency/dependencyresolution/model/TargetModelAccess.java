package com.neaterbits.util.concurrency.dependencyresolution.model;

public interface TargetModelAccess {

	TargetKey<?> getRootTarget();
	
	<TARGET> TargetDefinition<TARGET> getTargetDefinition(TargetKey<TARGET> targetKey);
}
