package org.jutils.concurrency.dependencyresolution.executor;

@FunctionalInterface
public interface OnBuildResult {

    void onResult(TargetBuildResult result);
}
