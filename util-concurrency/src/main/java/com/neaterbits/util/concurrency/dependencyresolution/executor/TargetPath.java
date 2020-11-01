package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

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
        
        final TargetEdge targetEdge = new TargetEdge(prerequisites, prerequisite, target);
        
        final List<TargetEdge> list = new ArrayList<>(path);
        list.add(targetEdge);
        
        return new TargetPath(list);
    }
}
