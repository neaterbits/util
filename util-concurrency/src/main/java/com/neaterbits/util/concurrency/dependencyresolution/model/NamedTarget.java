package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;

public final class NamedTarget extends TargetDefinition<String> {

    private final String name;

    public NamedTarget(
            LogContext logContext,
            String name,
            String description,
            List<Prerequisites> prerequisites,
            Action<String> action,
            ActionWithResult<String> actionWithResult) {

        super(
                logContext,
                name,
                name,
                new TargetKey<>(String.class, name),
                description,
                prerequisites,
                action,
                actionWithResult);
        
        Objects.requireNonNull(name);
        
        this.name = name;
    }
    
    @Override
    public String getLogIdentifier() {
        return name;
    }

    @Override
    public String getLogLocalIdentifier() {
        return name;
    }

    @Override
    public String getDebugString() {
        return getLogIdentifier();
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
    public String targetSimpleLogString() {
        return name;
    }

    @Override
    public String targetToLogString() {
        return name;
    }

    @Override
    public String toString() {
        return "NamedTarget [name=" + name + ", description=" + getDescription() + "]";
    }
}
