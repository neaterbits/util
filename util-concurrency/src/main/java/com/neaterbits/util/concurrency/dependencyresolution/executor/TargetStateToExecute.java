package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.function.BiConsumer;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

final class TargetStateToExecute<CONTEXT extends TaskContext>
			extends BaseTargetState<CONTEXT> implements TargetOps<CONTEXT> {

	public TargetStateToExecute(TargetDefinition<?> target, TargetExecutorLogger logger) {
		super(target, logger);
	}
	
	@Override
	Status getStatus() {
		return Status.TO_EXECUTE;
	}

	@Override
	public BaseTargetState<CONTEXT> onCheckPrerequisitesComplete(TargetExecutionContext<CONTEXT> context) {
		
		final PrerequisiteCompletion status = PrerequisiteCompleteChecker.hasCompletedPrerequisites(context.state, target);
		
		final BaseTargetState<CONTEXT> nextState;
		
		if (status.getStatus() == Status.SUCCESS) {
			
			if (context.logger != null) {
				context.logger.onScheduleTarget(target, status.getStatus(), context.state);
			}

			BaseCollector.collectFromSubTargetsAndSubProducts(context, target);

			final boolean hasActions = runAnyActionsAndCallOnCompleted(context, target, (exception, async) -> {
				schedule(state -> exception != null
						? state.onActionError(context, exception)
						: state.onActionPerformed(context));
			});
			
			if (hasActions) {
				nextState = new TargetStatePerformingActions<>(target, logger);
			}
			else {
			    nextState = testForRecursiveTargets(context);
			}
		}
		else if (status.getStatus() == Status.FAILED) {
			// context.state.moveTargetFromToExecuteToFailed(target);
			
			if (context.logger != null) {
				context.logger.onTargetDone(target, status.getException(), context.state);
			}
			
			onCompletedTarget(context, target, status.getException(), false);
			nextState = new TargetStateFailed<>(target, logger, status.getException());
		}
		else {
			nextState = this;
		}

		return nextState;
	}

	private boolean runAnyActionsAndCallOnCompleted(
			TargetExecutionContext<CONTEXT> context,
			TargetDefinition<?> target,
			BiConsumer<Exception, Boolean> onCompleted) {

        final boolean actionRun;
        
        if (target.isUpToDate(context.taskContext)) {
            actionRun = false;
	    }
        else {
    		final Action<?> action = target.getAction();
    		final ActionWithResult<?> actionWithResult = target.getActionWithResult();
    		
    		if (action != null) {
    			Actions.runOrScheduleAction(context, action, target, onCompleted);
    			
    			actionRun = true;
    		}
    		else if (actionWithResult != null) {
    			Actions.runOrScheduleActionWithResult(context, actionWithResult, target, onCompleted);
    			
    			actionRun = true;
    			
    		}
    		else {
    			actionRun = false;
    		}
        }
		
		return actionRun;
	}
}

