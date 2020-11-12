package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;

public final class FileTarget<TARGET> extends TargetDefinition<TARGET> {

	private static String getLogIdentifier(File file) {
		return file.getPath();
	}
	
	private static String getLogLocalIdentifier(File file) {
		return file.getName();
	}
	
	private final File file;
	
	public FileTarget(
			LogContext logContext,
			Class<TARGET> type,
			File file,
			String description,
			TARGET targetObject,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {
		
		super(
				logContext,
				getLogIdentifier(file),
				getLogLocalIdentifier(file),
				new TargetKey<>(type, targetObject),
				description,
				prerequisites,
				action,
				actionWithResult);

		Objects.requireNonNull(file);
		
		this.file = file;
	}

	@Override
	public String getDebugString() {
		return file.getName();
	}

	File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileTarget<?> other = (FileTarget<?>) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileTarget [file=" + file + "]";
	}
}
