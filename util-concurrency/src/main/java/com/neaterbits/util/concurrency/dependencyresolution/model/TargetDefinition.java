package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.structuredlog.binary.logging.Loggable;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;
import com.neaterbits.util.concurrency.dependencyresolution.executor.BuildEntity;

public abstract class TargetDefinition<TARGET> extends BuildEntity implements Loggable {

	private static final String LOG_FIELD_PREREQUISITES = "prerequisites";

	private final LogContext logContext;
	
    private final String logIdentifier;
    private final String logLocalIdentifier;
    
	private final int constructorLogSequenceNo;

	private final TargetKey<TARGET> targetKey;
	private final String description;

	private List<Prerequisites> prerequisites;
	private final Action<TARGET> action;
	private final ActionWithResult<TARGET> actionWithResult;

    protected abstract boolean isUpToDate(TARGET target, Collection<Prerequisites> prerequisites);
	
	protected TargetDefinition(
			LogContext logContext,
			String logIdentifier,
			String logLocalIdentifier,
            TargetKey<TARGET> targetKey,
			String description,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {
		
		if (
				(prerequisites == null || prerequisites.isEmpty())
			&& action == null
			&& actionWithResult == null
				
				) {

			throw new IllegalArgumentException("No action or prerequisites for target " + targetToLogString());
		}
		
		this.constructorLogSequenceNo = logConstructor(
				logContext,
				this,
				TargetDefinition.class,
				logIdentifier,
				logLocalIdentifier,
				description);
		
		this.logContext = logContext;
		this.logIdentifier = logIdentifier;
		this.logLocalIdentifier = logLocalIdentifier;
        this.targetKey = targetKey;
		this.description = description;
		this.prerequisites = logConstructorListField(logContext, LOG_FIELD_PREREQUISITES, prerequisites);
		this.action = action;
		this.actionWithResult = actionWithResult;
		
		updatePrerequisites(prerequisites);
	}
	
	public final boolean isUpToDate() {
	    
	    return isUpToDate(getTargetObject(), getPrerequisites());
	}

	@Override
	public final int getConstructorLogSequenceNo() {
		return constructorLogSequenceNo;
	}

	public final void print(PrintStream out) {
	    
	    out.append(getTargetObject().toString()).append(" : ");
	    
	    final LinkedHashSet<TargetDefinition<?>> subTargets = new LinkedHashSet<>();
	    
	    for (int i = 0; i < prerequisites.size(); ++ i) {

	        if (i > 0) {
                out.append(", ");
            }
	        
	        out.append("[");
	        
	        final Prerequisites prerequisites = this.prerequisites.get(i);
	        
	        int j = 0;
	        
	        for (Prerequisite<?> prerequisite : prerequisites.getPrerequisites()) {

	            if (j > 0) {
	                out.append(", ");
	            }
	            
	            out.append(prerequisite.getDebugString());
	     
	            if (prerequisite.getSubTarget() != null) {
	                subTargets.add(prerequisite.getSubTarget());
	            }
	            else {
	                throw new IllegalStateException("No sub target for prerequisite '" + prerequisite.getDebugString() + "'");
	            }
	            
	            ++ j;
	        }
	        
	        out.append("]");
	    }
	    
	    out.println();
	    
	    for (TargetDefinition<?> subTarget : subTargets) {
	        subTarget.print(out);
	    }
	}
	
	public final List<Prerequisites> getPrerequisites() {
		return prerequisites;
	}
	
	public void updatePrerequisites(List<Prerequisites> prerequisites) {

		this.prerequisites = Collections.unmodifiableList(prerequisites);
	}

	public final LogContext getLogContext() {
		return logContext;
	}
	
	final Class<TARGET> getType() {
		return targetKey.getType();
	}

	@Override
	public final String getDescription() {
		return description;
	}

	public final TARGET getTargetObject() {
		return targetKey.getTargetObject();
	}
	
	public final TargetKey<TARGET> getTargetKey() {
		return targetKey;
	}
	
	public final Action<TARGET> getAction() {
		return action;
	}

	public final ActionWithResult<TARGET> getActionWithResult() {
		return actionWithResult;
	}
	
	@Override
    public final String getLogIdentifier() {
        return logIdentifier;
    }

    @Override
    public final String getLogLocalIdentifier() {
        return logLocalIdentifier;
    }

    public final String targetSimpleLogString() {
        return getLogIdentifier();
    }
    
    public final String targetToLogString() {
        return getLogLocalIdentifier();
    }

    @Override
    public String getDebugString() {
        return getLogIdentifier();
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targetKey == null) ? 0 : targetKey.hashCode());
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
		final TargetDefinition<?> other = (TargetDefinition<?>) obj;
		if (targetKey == null) {
			if (other.targetKey != null)
				return false;
		} else if (!targetKey.equals(other.targetKey))
			return false;
		return true;
	}
}
