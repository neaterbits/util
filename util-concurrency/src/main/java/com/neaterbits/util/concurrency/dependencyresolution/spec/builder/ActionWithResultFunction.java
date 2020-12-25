package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

public interface ActionWithResultFunction<CONTEXT, TARGET, RESULT> {

	ActionResult<RESULT> perform(CONTEXT context, TARGET target, ActionParameters<TARGET> parameters);
	
}
