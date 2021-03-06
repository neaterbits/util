package org.jutils.concurrency.dependencyresolution.spec;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jutils.concurrency.dependencyresolution.model.Prerequisite;
import org.jutils.concurrency.dependencyresolution.model.Prerequisites;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TargetFinderLogger {

	<CONTEXT extends TaskContext, TARGET>
	void onFindTarget(int indent, CONTEXT context, TargetSpec<CONTEXT, TARGET> targetSpec, TARGET target);

	void onGetPrerequisites(int indent, String target, Collection<?> prerequisites);

	void onFoundPrerequisites(int indent, TargetDefinition<?> target, List<Prerequisites> prerequisites);
	
	<CONTEXT extends TaskContext, TARGET, PREREQUISITE>
	void onPrerequisites(
			int indent,
			TargetSpec<CONTEXT, TARGET> targetSpec,
			TARGET target,
			PrerequisiteSpec<CONTEXT, TARGET, PREREQUISITE> prerequisiteSpec,
			Set<Prerequisite<?>> prerequisites);
}
