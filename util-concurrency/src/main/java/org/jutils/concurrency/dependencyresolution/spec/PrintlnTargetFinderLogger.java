package org.jutils.concurrency.dependencyresolution.spec;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.jutils.Indent;
import org.jutils.concurrency.dependencyresolution.model.Prerequisite;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

public class PrintlnTargetFinderLogger implements TargetFinderLogger {

    private static final Boolean DEBUG_FIND_PREREREQUISITE = false;
    private static final Boolean DEBUG_FOUND_PREREREQUISITES = true;
    
	@Override
	public <CONTEXT extends TaskContext, TARGET> void onFindTarget(
			int indent,
			CONTEXT context,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target) {

		final String targetType = targetSpec.getType() != null
				? targetSpec.getType().getSimpleName()
				: "null";

		final String targetFile;

		if (targetSpec instanceof FileTargetSpec) {

			@SuppressWarnings("unchecked")
			final FileTargetSpec<CONTEXT, TARGET, ?> fileTargetSpec = (FileTargetSpec<CONTEXT, TARGET, ?>)targetSpec;

			if (fileTargetSpec.getFile() != null) {

				final Object fileTarget = fileTargetSpec.getFileTarget().apply(context, target);

				@SuppressWarnings({ "unchecked", "rawtypes" })
				final Function<Object, File> getFile = (Function)fileTargetSpec.getFile();

				targetFile = getFile.apply(fileTarget).getPath();
			}
			else {
				targetFile = "null";
			}

			System.out.println(Indent.indent(indent) + "Find file target type=" + targetType + ", file=" + targetFile);
		}
		else if (targetSpec instanceof InfoTargetSpec) {
		    
		    final InfoTargetSpec<CONTEXT, TARGET> infoTargetSpec = (InfoTargetSpec<CONTEXT, TARGET>)targetSpec;

			System.out.println(Indent.indent(indent) + "Find info target type=" + targetType + ", " + infoTargetSpec.getDescription(target));
		}
        else if (targetSpec instanceof NamedTargetSpec) {
            
            @SuppressWarnings("unchecked")
            final NamedTargetSpec<CONTEXT> namedTargetSpec = (NamedTargetSpec<CONTEXT>)targetSpec;

            System.out.println(Indent.indent(indent) + "Find name target type=" + targetType + ", name=" + namedTargetSpec.getName());
        }
        else if (targetSpec instanceof UpToDateTargetSpec<?, ?>) {
            
            final UpToDateTargetSpec<CONTEXT, TARGET> upToDateTargetSpec = (UpToDateTargetSpec<CONTEXT, TARGET>)targetSpec;

            System.out.println(Indent.indent(indent) + "Find up to date target type=" + upToDateTargetSpec.getDescription(target));
        }
        else {
            throw new IllegalStateException();
        }
	}

	@Override
    public void onGetPrerequisites(int indent, String target, Collection<?> prerequisites) {
        
        System.out.println(Indent.indent(indent) + "Got " + prerequisites.size() + " prerequisites for target " + target
                    + " " + prerequisites);
    }

    @Override
	public <CONTEXT extends TaskContext, TARGET, PREREQUISITE> void onPrerequisites(
			int indent,
			TargetSpec<CONTEXT, TARGET> targetSpec, TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec, Set<Prerequisite<?>> prerequisites) {

	    if (DEBUG_FIND_PREREREQUISITE) {
	        System.out.println(Indent.indent(indent) + "Prerequisites " + prerequisiteSpec.getDescription());
	    }
	}

	@Override
	public void onFoundPrerequisites(int indent, TargetDefinition<?> target, List<Prerequisites> prerequisites) {

	    if (DEBUG_FOUND_PREREREQUISITES) {
	        System.out.println(Indent.indent(indent) + "Found prerequisites for target " + target.getDescription() + " prerequisites " 
	    
	                    + prerequisites.size());
	    }
	}
}
