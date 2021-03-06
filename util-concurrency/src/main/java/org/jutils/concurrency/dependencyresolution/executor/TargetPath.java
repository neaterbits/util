package org.jutils.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jutils.concurrency.dependencyresolution.model.Prerequisite;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;

final class TargetPath {

    private final List<TargetEdge> path;

    TargetPath(TargetDefinition<?> rootTarget) {
        this(Arrays.asList(new TargetEdge(rootTarget)));
    }

    private TargetPath(List<TargetEdge> path) {
        this.path = Collections.unmodifiableList(path);
    }
    
    TargetPath add(
            Prerequisites prerequisites,
            Prerequisite<?> prerequisite,
            TargetDefinition<?> target) {
        
        final TargetEdge targetEdge = new TargetEdge(
                getLastEdge(),
                prerequisites,
                prerequisite,
                target);
        
        final List<TargetEdge> list = new ArrayList<>(path);
        list.add(targetEdge);
        
        return new TargetPath(list);
    }
    
    boolean isEmpty() {
        return path.isEmpty();
    }

    TargetEdge getLastEdge() {
        return path.get(path.size() - 1);
    }

    TargetEdge getLastEdge(int offset) {
        return path.get(path.size() - 1 - offset);
    }
}
