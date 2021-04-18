package org.jutils.concurrency.dependencyresolution.executor;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.scheduling.task.TaskContext;

public class RecursiveBuildSpec<CONTEXT extends TaskContext, TARGET, PREREQUISITE> {

    private final BiFunction<CONTEXT, TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites;
    private final Function<PREREQUISITE, TARGET> getDependencyFromPrerequisite;
    
    public RecursiveBuildSpec(
            BiFunction<CONTEXT, TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisites,
            Function<PREREQUISITE, TARGET> getDependencyFromPrerequisite) {

        Objects.requireNonNull(getSubPrerequisites);
        Objects.requireNonNull(getDependencyFromPrerequisite);

        this.getSubPrerequisites = getSubPrerequisites;
        this.getDependencyFromPrerequisite = getDependencyFromPrerequisite;
    }

    public final BiFunction<CONTEXT, TARGET, SubPrerequisites<PREREQUISITE>> getSubPrerequisitesFunction() {
        return getSubPrerequisites;
    }
    
    public final Function<PREREQUISITE, TARGET> getTargetFromPrerequisiteFunction() {
        return getDependencyFromPrerequisite;
    }
}
