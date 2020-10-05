package com.neaterbits.structuredlog.binary.model;

public final class ScalarLogObjectField extends LogField {

	private Object value;

	public ScalarLogObjectField(int logFileSequenceNo, int constructorSequenceNo, LogObject parent, String fieldName, Object value) {
		super(logFileSequenceNo, constructorSequenceNo, parent, fieldName);

		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	void setValue(Object value) {
		this.value = value;
	}
}
