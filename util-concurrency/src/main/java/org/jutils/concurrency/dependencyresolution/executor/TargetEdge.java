package org.jutils.concurrency.dependencyresolution.executor;

import java.util.Objects;

import org.jutils.concurrency.dependencyresolution.model.Prerequisite;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;

final class TargetEdge {
    
    enum Type {
        ROOT,
        REGULAR,
        RECURSIVE_ROOT,
        RECURSIVE_SUB
    }

    private final Prerequisites prerequisites;
    private final Prerequisite<?> prerequisite;
    private final TargetDefinition<?> target;
    
    private final Type type;

    TargetEdge(TargetDefinition<?> target) {

        Objects.requireNonNull(target);
        
        this.prerequisites = null;
        this.prerequisite = null;
        this.target = target;
        this.type = Type.ROOT;
    }

    TargetEdge(
            TargetEdge prevEdge,
            Prerequisites prerequisites,
            Prerequisite<?> prerequisite,
            TargetDefinition<?> target) {

        Objects.requireNonNull(prerequisites);
        Objects.requireNonNull(prerequisite);
        Objects.requireNonNull(target);

        this.prerequisites = prerequisites;
        this.prerequisite = prerequisite;
        this.target = target;
        
        if (prerequisites.getRecursiveBuildInfo() != null) {
        
            switch (prevEdge.type) {
            case RECURSIVE_ROOT:
            case RECURSIVE_SUB:
                this.type = Type.RECURSIVE_SUB;
                break;
                
            case ROOT:
            case REGULAR:
                this.type = Type.RECURSIVE_ROOT;
                break;
                
            default:
                throw new IllegalStateException();
            }
            
        }
        else {
            this.type = Type.REGULAR;
        }
    }

    Prerequisites getPrerequisites() {
        return prerequisites;
    }

    Prerequisite<?> getPrerequisite() {
        return prerequisite;
    }

    TargetDefinition<?> getTarget() {
        return target;
    }

    Type getType() {
        return type;
    }
}
