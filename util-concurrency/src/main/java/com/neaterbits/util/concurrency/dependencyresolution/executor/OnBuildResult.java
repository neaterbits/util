package com.neaterbits.util.concurrency.dependencyresolution.executor;

@FunctionalInterface
public interface OnBuildResult {

    void onResult(TargetBuildResult result);
}
