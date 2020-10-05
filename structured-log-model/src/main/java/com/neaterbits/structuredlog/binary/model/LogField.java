package com.neaterbits.structuredlog.binary.model;

import java.util.Objects;

public abstract class LogField extends LogNode {

	private final int constructorSequenceNo;
	private final String fieldName;

	LogField(int logFileSequenceNo, int constructorSequenceNo, LogObject parent, String fieldName) {
		super(logFileSequenceNo, parent);
		
		Objects.requireNonNull(fieldName);

		if (constructorSequenceNo != parent.getConstructorLogSequenceNo()) {
			throw new IllegalArgumentException();
		}
		
		this.constructorSequenceNo = constructorSequenceNo;
		this.fieldName = fieldName;
	}

	public final int getConstructorSequenceNo() {
		return constructorSequenceNo;
	}

	public final String getFieldName() {
		return fieldName;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + constructorSequenceNo;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogField other = (LogField) obj;
		if (constructorSequenceNo != other.constructorSequenceNo)
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
}
