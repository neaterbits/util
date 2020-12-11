package com.neaterbits.util.concurrency.dependencyresolution.model;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.Action;
import com.neaterbits.util.concurrency.dependencyresolution.executor.ActionWithResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class FileTarget<TARGET> extends TargetDefinition<TARGET> {

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
			String semanticType,
			File file,
            String semanticAction,
			String description,
			TARGET targetObject,
			List<Prerequisites> prerequisites,
			Action<TARGET> action,
			ActionWithResult<TARGET> actionWithResult) {
		
		super(
				logContext,
				new TargetKey<>(type, targetObject),
				prerequisites,
				action,
				actionWithResult,
                new TargetDebug(
                        semanticType,
                        getLogIdentifier(file),
                        getLogLocalIdentifier(file),
                        semanticAction,
                        description,
                        true));

		Objects.requireNonNull(file);
		
		this.file = file;
	}

	@Override
    protected <CONTEXT extends TaskContext>
	boolean isUpToDate(CONTEXT context, TARGET target, Collection<Prerequisites> prerequisites) {

	    boolean rebuild;
        
        if (!file.exists()) {
            rebuild = true;
        }
        else if (prerequisites.isEmpty()) {
            rebuild = false;
        }
        else {
            rebuild = false;
            
            for (Prerequisites prereq : prerequisites) {
                
                for (Prerequisite<?> p : prereq.getPrerequisites()) {
                
                    final File filePrereq;
                    
                    if (p.getSourceFile() != null) {
                        filePrereq = p.getSourceFile();
                    }
                    else if (p.getSubTarget() != null && p.getSubTarget().getTargetObject() != null) {
                        filePrereq = (File)p.getSubTarget().getTargetObject();
                    }
                    else {
                        filePrereq = null;
                    }
                    
                    if (filePrereq != null && filePrereq.exists() && filePrereq.lastModified() > file.lastModified()) {
                        rebuild = true;
                        break;
                    }
                }
            }
        }

        return !rebuild;
	}

	public final String getDebugString() {
		return file.getName();
	}

	final File getFile() {
		return file;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
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
	public final String toString() {
		return "FileTarget [file=" + file + "]";
	}
}
