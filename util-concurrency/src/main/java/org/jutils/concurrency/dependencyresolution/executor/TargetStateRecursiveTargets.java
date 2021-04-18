package org.jutils.concurrency.dependencyresolution.executor;

import org.jutils.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.scheduling.task.TaskContext;

final class TargetStateRecursiveTargets<CONTEXT extends TaskContext> extends BaseTargetState<CONTEXT> {

	TargetStateRecursiveTargets(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
	}

	@Override
	Status getStatus() {
		return Status.ACTION_PERFORMED_COLLECTING_SUBTARGETS;
	}

	@Override
	public BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context) {

		final BaseTargetState<CONTEXT> nextState;
		
		final PrerequisiteCompletion completion = PrerequisiteCompleteChecker.hasCompletedPrerequisites(context.state, target);
		
		logger.onCheckRecursiveTargetsComplete(target, completion.getStatus());
		
		switch (completion.getStatus()) {
		case SUCCESS:
			BaseCollector.collectFromSubTargetsAndSubProducts(context, target);

			onCompletedTarget(context, target, completion.getException(), false);
			nextState = new TargetStateDone<>(target, logger);
			break;

		case FAILED:
			onCompletedTarget(context, target, completion.getException(), false);
			nextState = new TargetStateFailed<>(target, logger, completion.getException());
			break;
			
		default:
			nextState = this;
			break;
		}

		return nextState;
	}
}
