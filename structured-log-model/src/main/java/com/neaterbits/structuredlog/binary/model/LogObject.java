package com.neaterbits.structuredlog.binary.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.Loggable;


public final class LogObject extends LogNode implements Loggable {

	private final int constructorSequenceNo;
	private final int identityHashCode;
	private final int hashCode;
	private final String type;
	private final String identifier;
	private final String localIdentifier;
	private final String description;
	
	private Map<String, LogField> fields;
	
	public LogObject(
			int logFilesequenceNo,
			int constructorSequenceNo,
			LogField parent,
			int identityHashCode,
			int hashCode,
			String type,
			String identifier,
			String localIdentifier,
			String description) {
		super(logFilesequenceNo, parent);

		this.constructorSequenceNo = constructorSequenceNo;
		this.identityHashCode = identityHashCode;
		this.hashCode = hashCode;
		this.type = type;
		this.identifier = identifier;
		this.localIdentifier = localIdentifier;
		this.description = description;
	}

	void setParent(LogField field) {
		
		Objects.requireNonNull(field);
	
		super.setParentNode(field);
	}
	
	void addField(String fieldName, LogField field) {
		
		Objects.requireNonNull(fieldName);
		Objects.requireNonNull(field);
		
		if (fields == null) {
			this.fields = new HashMap<>();
		}
		else {
			if (fields.containsKey(fieldName)) {
				throw new IllegalArgumentException();
			}
		}
		
		fields.put(fieldName, field);
	}
	
	public int getIdentityHashCode() {
		return identityHashCode;
	}

	public int getHashCode() {
		return hashCode;
	}

	public String getType() {
		return type;
	}

	public String getSimpleType() {
		final int index = type.lastIndexOf('.');
		
		return type.substring(index + 1);
	}
	
	@Override
	public int getConstructorLogSequenceNo() {
		return getSequenceNo();
	}

	@Override
	public String getLogIdentifier() {
		return identifier;
	}

	@Override
	public String getLogLocalIdentifier() {
		return localIdentifier;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Collection<LogField> getFields() {
		return fields != null ? fields.values() : null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + constructorSequenceNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogObject other = (LogObject) obj;
		if (constructorSequenceNo != other.constructorSequenceNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LogObject [type=" + type + ", identifier=" + identifier + ", localIdentifier=" + localIdentifier +", description=" + description
				+ ", fields=" + (fields != null ? fields.keySet() : null) + "]";
	}	
}
