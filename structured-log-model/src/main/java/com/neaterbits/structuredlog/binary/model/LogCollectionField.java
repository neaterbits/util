package com.neaterbits.structuredlog.binary.model;

import java.util.ArrayList;
import java.util.Collection;

public class LogCollectionField extends LogField {

	private Collection<LogObject> collection;
	
	public LogCollectionField(int logFileSequenceNo, int constructorSequenceNo, LogObject parent, String fieldName) {
		super(logFileSequenceNo, constructorSequenceNo, parent, fieldName);
	}

	public LogCollectionField(int logFileSequenceNo, int constructorSequenceNo, LogObject parent, String fieldName, Collection<LogObject> collection) {
		super(logFileSequenceNo, constructorSequenceNo, parent, fieldName);
		
		this.collection = new ArrayList<>(collection);
		
		collection.forEach(logObject -> logObject.setParent(this));
	}

	public Collection<LogObject> getCollection() {
		return collection;
	}

	@Override
	public String toString() {
		return "LogCollectionField [getSequenceNo()=" + getSequenceNo() + ", collection=" + collection + "]";
	}
}
