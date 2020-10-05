package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;


public interface PrerequisitesMap<TARGET> {

	<T> T getCollectedProduct(TARGET target, Class<T> type);
	
}
