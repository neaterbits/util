package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;

// All paths to evaluating a target
final class TargetPaths {

    private final List<TargetPath> paths;

    TargetPaths(TargetPath path) {
        
        Objects.requireNonNull(path);
        
        this.paths = Collections.unmodifiableList(Arrays.asList(path));
    }
    
    private TargetPaths(List<TargetPath> paths) {
        
        this.paths = Collections.unmodifiableList(paths);
    }
    
    TargetPaths add(
            Prerequisites prerequisites,
            Prerequisite<?> prerequisite,
            TargetDefinition<?> target) {
        
        final List<TargetPath> added = this.paths.stream()
                .map(path -> path.add(prerequisites, prerequisite, target))
                .collect(Collectors.toList());
        
        return new TargetPaths(added);
    }
}
