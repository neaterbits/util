package org.jutils.concurrency.dependencyresolution.spec.builder;

public final class ActionResult<RESULT> {

	private final RESULT result;
	private final ActionLog log;
	
	public ActionResult(RESULT result, ActionLog log) {
		this.result = result;
		this.log = log;
	}

	public RESULT getResult() {
		return result;
	}

	public ActionLog getLog() {
		return log;
	}
}
