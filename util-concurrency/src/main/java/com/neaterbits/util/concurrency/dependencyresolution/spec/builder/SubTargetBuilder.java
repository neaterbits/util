package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface SubTargetBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITES_BUILDER> {

	PREREQUISITES_BUILDER addNamedSubTarget(String name, Function<TARGET, String> description);

	PREREQUISITES_BUILDER addInfoSubTarget(Class<TARGET> type, Function<TARGET, String> description);

	PREREQUISITES_BUILDER addInfoSubTarget(Class<TARGET> type, String name, Function<TARGET, String> qualifierName, Function<TARGET, String> description);

	PREREQUISITES_BUILDER addFileSubTarget(Class<TARGET> type, Function<TARGET, File> file, Function<TARGET, String> description);
	
	<FILE_TARGET> PREREQUISITES_BUILDER addFileSubTarget(
			Class<TARGET> type,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET>
			getFileTarget, Function<FILE_TARGET, File> file,
			Function<TARGET, String> description);

}
