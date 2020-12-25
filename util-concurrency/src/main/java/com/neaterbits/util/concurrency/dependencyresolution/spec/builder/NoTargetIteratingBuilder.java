package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface NoTargetIteratingBuilder<CONTEXT extends TaskContext> {
	
	/*
	<PREREQUISITE, TARGET>
	PrerequisiteActionBuilder<CONTEXT, TARGET, PREREQUISITE> iterating(BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites);
	*/

	<PREREQUISITE>
	NoTargetPrerequisiteBuilder<CONTEXT, PREREQUISITE> fromIterating(Function<CONTEXT, Collection<PREREQUISITE>> getPrerequisites);

	/*
    <PRODUCT, PREREQUISITE>
    NoTargetPrerequisiteBuilder<CONTEXT, PREREQUISITE> fromIteratingProduct(
            Class<PRODUCT> productType,
            BiFunction<CONTEXT, PrerequisitesMap<PRODUCT>, Collection<PREREQUISITE>> getPrerequisites);
    */
}
