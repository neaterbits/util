package com.neaterbits.util.concurrency.dependencyresolution.executor;

public enum Status {

	TO_EXECUTE,
	SCHEDULED,
	
	ACTION_PERFORMED_COLLECTING_SUBTARGETS,

	SUCCESS,
	FAILED,

}
