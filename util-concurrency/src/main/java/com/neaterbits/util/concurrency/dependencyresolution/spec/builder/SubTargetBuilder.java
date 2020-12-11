package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.neaterbits.util.concurrency.dependencyresolution.model.UpToDate;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface SubTargetBuilder<CONTEXT extends TaskContext, TARGET, PREREQUISITES_BUILDER> {

	PREREQUISITES_BUILDER addNamedSubTarget(
	                            String name,
	                            String semanticType,
	                            String semanticAction,
	                            String description);

	PREREQUISITES_BUILDER addInfoSubTarget(
	                            Class<TARGET> type,
	                            String semanticType,
	                            String semanticAction,
	                            Function<TARGET, String> getIdentifier,
	                            Function<TARGET, String> getDescription);

	PREREQUISITES_BUILDER addFileSubTarget(
	                            Class<TARGET> type,
	                            String semanticType,
	                            String semanticAction,
	                            Function<TARGET, File> getfile,
	                            Function<TARGET, String> getDescription);
	
	<FILE_TARGET> PREREQUISITES_BUILDER addFileSubTarget(
			Class<TARGET> type,
			String semanticType,
			String semanticAction,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET>
			getFileTarget, Function<FILE_TARGET, File> file,
			Function<TARGET, String> description);

    PREREQUISITES_BUILDER addFilesSubTarget(
            Class<TARGET> type,
            String semanticType,
            String semanticAction,
            UpToDate<CONTEXT, TARGET> upToDate,
            Function<TARGET, String> getIdentifier,
            Function<TARGET, String> getDescription);
}
