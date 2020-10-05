package com.neaterbits.structuredlog.binary.model;


public final class LoggableField extends LogField {

	private final LogObject object;

	public LoggableField(int logFileSequenceNo, int constructorSequenceNo, LogObject parent, String fieldName, LogObject object) {
		super(logFileSequenceNo, constructorSequenceNo, parent, fieldName);
	
		object.setParent(this);
		
		this.object = object;
	}

	public LogObject getObject() {
		return object;
	}
}
