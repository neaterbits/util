package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public interface ResultTypedPrerequisitesBuilder<CONTEXT extends TaskContext, TARGET, RESULT>
	extends TargetPrerequisitesBuilder<TaskContext, TARGET> {

	<PREREQUISITE, PREREQUISITE_RESULT, SUB_RESULT>
	TypedPrerequisitesBuilder<CONTEXT, TARGET> withPrerequisites(
			String description,
			Class<SUB_RESULT> resultType,
			Class<PREREQUISITE_RESULT> prerequisiteResultType,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			Consumer<ResultSubTargetBuilder<CONTEXT, PREREQUISITE, PREREQUISITE_RESULT>> prerequisiteTargets,
			BiFunction<TARGET, List<PREREQUISITE_RESULT>, SUB_RESULT> makeResult);

	ResultProcessor<CONTEXT, TARGET, RESULT> ioBound(BiFunction<CONTEXT, TARGET, RESULT> process);
	
	ResultProcessor<CONTEXT, TARGET, RESULT> cpuBound(BiFunction<CONTEXT, TARGET, RESULT> process);

}
