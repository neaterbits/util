package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.io.File;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;
import com.neaterbits.util.concurrency.dependencyresolution.executor.BuildEntity;

public final class Prerequisite<PREREQUISITE> extends BuildEntity implements Loggable {

	private static final String LOG_FIELD_SUBTARGET = "subTarget";

	private final int constructorLogSequenceNo;
	
	private final PREREQUISITE item;

	// One of the below
	private final File sourceFile;
	private final TargetDefinition<PREREQUISITE> subTarget;
	
	public Prerequisite(LogContext logContext, PREREQUISITE item, File sourceFile) {
		this(logContext, item, sourceFile, null);
	}

	public Prerequisite(LogContext logContext, PREREQUISITE item, TargetDefinition<PREREQUISITE> subTarget) {
		this(logContext, item, null, subTarget);
	}
		
	private Prerequisite(LogContext logContext, PREREQUISITE item, File sourceFile, TargetDefinition<PREREQUISITE> subTarget) {
		this.constructorLogSequenceNo = logConstructor(logContext, this, getClass(), null, null, null);
		
		Objects.requireNonNull(item);
		
		this.item = item;
		
		this.sourceFile = sourceFile;
		
		if (subTarget == null) {
			this.subTarget = null;
		}
		else {
			// Log target definition directly
			logConstructorLoggableField(logContext, null, LOG_FIELD_SUBTARGET, subTarget);

			this.subTarget = subTarget;
		}
	}

    public Prerequisite(LogContext logContext, Prerequisite<PREREQUISITE> other, TargetDefinition<PREREQUISITE> subTarget) {
        this(logContext, other.item, subTarget);
    }

    @Override
	public int getConstructorLogSequenceNo() {
		return constructorLogSequenceNo;
	}

	@Override
	public String getLogIdentifier() {
		return null;
	}

	@Override
	public String getLogLocalIdentifier() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getDebugString() {
		return null;
	}

	public PREREQUISITE getItem() {
		return item;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public TargetDefinition<PREREQUISITE> getSubTarget() {
		return subTarget;
	}

	@Override
	public String toString() {
		return item.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
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
		Prerequisite<?> other = (Prerequisite<?>) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}
}
