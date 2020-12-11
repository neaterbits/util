package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class NamedTarget extends TargetDefinition<String> {

    private final String name;

    public NamedTarget(
            LogContext logContext,
            String semanticType,
            String name,
            String semanticAction,
            String description,
            List<Prerequisites> prerequisites,
            Action<String> action,
            ActionWithResult<String> actionWithResult) {

        super(
                logContext,
                new TargetKey<>(String.class, name),
                prerequisites,
                action,
                actionWithResult,
                new TargetDebug(
                        semanticType,
                        name,
                        name,
                        semanticAction,
                        description,
                        true));
        
        Objects.requireNonNull(name);
        
        this.name = name;
    }
    
    @Override
    protected <CONTEXT extends TaskContext>
    boolean isUpToDate(CONTEXT context, String target, Collection<Prerequisites> prerequisites) {
        return false;
    }

    @Override
    protected String getTypeString() {
        return "named";
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NamedTarget other = (NamedTarget) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "NamedTarget [name=" + name + ", description=" + getDescription() + "]";
    }
}
