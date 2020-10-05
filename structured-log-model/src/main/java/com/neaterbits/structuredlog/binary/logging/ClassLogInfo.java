package com.neaterbits.structuredlog.binary.logging;

final class ClassLogInfo {
	
	private final int logTypeId;

	ClassLogInfo(int logTypeId) {
		this.logTypeId = logTypeId;
	}

	int getLogTypeId() {
		return logTypeId;
	}
}
