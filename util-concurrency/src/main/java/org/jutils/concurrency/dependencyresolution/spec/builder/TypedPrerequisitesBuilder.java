package org.jutils.concurrency.dependencyresolution.spec.builder;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jutils.concurrency.scheduling.task.TaskContext;

public interface TypedPrerequisitesBuilder<CONTEXT extends TaskContext, TARGET> 
	extends
		ActionWithoutResultBuilder<CONTEXT, TARGET> {

	<PREREQUISITE, PREREQUISITE_RESULT, SUB_RESULT>
	TypedPrerequisitesBuilder<CONTEXT, TARGET> prerequisites(
			String description,
			Class<SUB_RESULT> resultType,
			Class<PREREQUISITE_RESULT> prerequisiteResultType,
			BiFunction<CONTEXT, TARGET, Collection<PREREQUISITE>> getPrerequisites,
			Consumer<ResultSubTargetBuilder<CONTEXT, PREREQUISITE, PREREQUISITE_RESULT>> prerequisiteTargets,
			BiFunction<TARGET, List<PREREQUISITE_RESULT>, SUB_RESULT> makeResult);

	void actionWithResultProcessing(Consumer<ActionWithResultBuilder<CONTEXT, TARGET>> builder);
}
