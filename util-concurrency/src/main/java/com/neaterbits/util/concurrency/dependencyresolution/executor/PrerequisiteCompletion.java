package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

final class PrerequisiteCompletion {

	private final Status status;
	private final Exception exception;

	PrerequisiteCompletion(Status status) {
		this(status, null);
	}
	
	PrerequisiteCompletion(Status status, Exception exception) {
		
		Objects.requireNonNull(status);
		
		if (status == Status.FAILED) {
			Objects.requireNonNull(exception);
		}
		else {
			if (exception != null) {
				throw new IllegalArgumentException();
			}
		}
		
		this.status = status;
		this.exception = exception;
	}

	public Status getStatus() {
		return status;
	}

	public Exception getException() {
		return exception;
	}
}
