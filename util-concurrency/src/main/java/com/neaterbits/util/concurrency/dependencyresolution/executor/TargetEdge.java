package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Objects;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

final class TargetEdge {

    private final Prerequisites prerequisites;
    private final Prerequisite<?> prerequisite;
    private final TargetDefinition<?> target;

    TargetEdge(TargetDefinition<?> target) {

        Objects.requireNonNull(target);
        
        this.prerequisites = null;
        this.prerequisite = null;
        this.target = target;
    }

    TargetEdge(
            Prerequisites prerequisites,
            Prerequisite<?> prerequisite,
            TargetDefinition<?> target) {

        Objects.requireNonNull(prerequisites);
        Objects.requireNonNull(prerequisite);
        Objects.requireNonNull(target);

        this.prerequisites = prerequisites;
        this.prerequisite = prerequisite;
        this.target = target;
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
}
