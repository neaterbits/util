package org.jutils.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jutils.concurrency.dependencyresolution.model.FileTarget;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.Constraint;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;
import org.jutils.structuredlog.binary.logging.LogContext;

public final class FileTargetSpec<CONTEXT extends TaskContext, TARGET, FILE_TARGET>
			extends TargetSpec<CONTEXT, TARGET> {

	private final Class<FILE_TARGET> fileTargetType;
	private final BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget;
	private final Function<FILE_TARGET, File> file;

	public FileTargetSpec(
			Class<TARGET> type,
			String semanticType,
			String semanticAction,
			Class<FILE_TARGET> fileTargetType,
			BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget,
			Function<FILE_TARGET, File> file,
			Function<TARGET, String> description,
			List<PrerequisiteSpec<CONTEXT, TARGET, ?>> prerequisites,
			Constraint constraint,
			ActionFunction<CONTEXT, TARGET> actionFunction,
			ActionWithResultFunction<CONTEXT, TARGET, ?> actionWithResult,
			ProcessResult<CONTEXT, TARGET, ?> onResult) {
		
		super(
		        type,
		        semanticType,
		        semanticAction,
		        description,
		        prerequisites,
		        constraint,
		        actionFunction,
		        actionWithResult,
		        onResult);
		
		this.fileTargetType = fileTargetType;
		this.getFileTarget = getFileTarget;
		this.file = file;
	}
	
	private FileTargetSpec(FileTargetSpec<CONTEXT, TARGET, FILE_TARGET> other, List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {
		super(other, additionalPrerequisites);
		
		this.fileTargetType = other.fileTargetType;
		this.getFileTarget = other.getFileTarget;
		this.file = other.file;
	}
	
	@Override
	public TargetSpec<CONTEXT, TARGET> addPrerequisiteSpecs(List<PrerequisiteSpec<CONTEXT, TARGET, ?>> additionalPrerequisites) {

		return new FileTargetSpec<>(this, additionalPrerequisites);
	}

	Class<FILE_TARGET> getFileTargetType() {
		return fileTargetType;
	}

	BiFunction<CONTEXT, TARGET, FILE_TARGET> getFileTarget() {
		return getFileTarget;
	}

	Function<FILE_TARGET, File> getFile() {
		return file;
	}

	@Override
	TargetDefinition<TARGET> createTargetDefinition(LogContext logContext, CONTEXT context, TARGET target, List<Prerequisites> prerequisitesList) {
		
		final FILE_TARGET fileTarget = getFileTarget.apply(context, target);
		
		return new FileTarget<>(
				logContext,
				getType(),
				getSemanticType(),
				file.apply(fileTarget),
                getSemanticAction(),
				getDescriptionFunction().apply(target),
				target,
				prerequisitesList,
				makeAction(),
				makeActionWithResult());
	}
}
