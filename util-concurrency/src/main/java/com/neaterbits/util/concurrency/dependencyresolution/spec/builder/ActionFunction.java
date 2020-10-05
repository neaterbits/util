package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

public interface ActionFunction<CONTEXT, TARGET> {

	ActionLog perform(CONTEXT context, TARGET target, ActionParameters<TARGET> actionParameters) throws Exception;
	
}
