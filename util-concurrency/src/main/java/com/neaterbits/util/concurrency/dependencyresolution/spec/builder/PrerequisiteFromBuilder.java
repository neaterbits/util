package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface PrerequisiteFromBuilder<CONTEXT extends TaskContext, FROM> {

	void withFile(Function<FROM, File> withFile);
	
}
