package org.jutils.concurrency.dependencyresolution.spec.builder;


public interface PrerequisitesMap {

	<TARGET, T> T getCollectedProduct(TARGET target, Class<T> type);
	
    <TARGET, T> T getTargetActionResult(TARGET target, Class<T> type);
}
