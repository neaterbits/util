package com.neaterbits.structuredlog.binary.model;

import java.util.Objects;

public abstract class LogNode {

	private final int logFileSequenceNo;
	
	private LogNode parent;

	public LogNode(int sequenceNo, LogNode parent) {
		this.logFileSequenceNo = sequenceNo;
		this.parent = parent;
	}

	public final int getSequenceNo() {
		return logFileSequenceNo;
	}
	
	public final LogNode getParent() {
		return parent;
	}
	
	final void setParentNode(LogNode node) {
		Objects.requireNonNull(node);
		
		if (this.parent != null) {
			throw new IllegalStateException("Already set");
		}
	}
}
