package com.neaterbits.structuredlog.binary.model;

import java.util.Objects;

public final class LogMessage {

	private final int sequenceNo;
	private final Severity severity;
	private final LogObject target;
	private final String message;
	
	public LogMessage(int sequenceNo, Severity severity, LogObject target, String message) {

		Objects.requireNonNull(severity);
		Objects.requireNonNull(target);
		Objects.requireNonNull(message);
		
		this.sequenceNo = sequenceNo;
		this.severity = severity;
		this.target = target;
		this.message = message;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public Severity getSeverity() {
		return severity;
	}

	public LogObject getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}
}
