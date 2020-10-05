package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.util.Objects;

import com.neaterbits.util.concurrency.scheduling.task.ProcessResult;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class ResultProcessorImpl<CONTEXT extends TaskContext, TARGET, FILE_TARGET, RESULT>
	implements ResultProcessor<CONTEXT, TARGET, RESULT> {

	private final TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState;
	
	ResultProcessorImpl(TargetBuilderState<CONTEXT, TARGET, FILE_TARGET> targetBuilderState) {
		
		Objects.requireNonNull(targetBuilderState);

		this.targetBuilderState = targetBuilderState;
	}

	@Override
	public void processResult(ProcessResult<CONTEXT, TARGET, RESULT> onResult) {
		targetBuilderState.setOnResult(onResult);
	}
}
